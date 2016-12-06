
package org.galatea.starter;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.galatea.starter.utils.jms.FuseJmsListenerContainerFactory;
import org.galatea.starter.utils.jms.FuseMessageHandlerMethodFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

@Configuration
@EnableJms
@Slf4j
public class JmsConfig implements JmsListenerConfigurer {

  // The number of concurrent threads that can pull messages off the queue
  @Value("${jms.listener-concurrency}")
  protected String listenerConcurrency;

  // The number of concurrent threads that can pull messages off the queue
  @Value("${jms.max-retry}")
  protected Integer maxRetryCount;


  @Bean
  public MessageConverter jacksonJmsMessageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    return converter;
  }

  @Bean
  public JmsListenerContainerFactory<?> jmsListenerContainerFactory(
      final ConnectionFactory queueConnectionFactory,
      final DefaultJmsListenerContainerFactoryConfigurer configurer) {

    CachingConnectionFactory cachingQueueConnFactory =
        new CachingConnectionFactory(queueConnectionFactory);
    FuseJmsListenerContainerFactory listenerFactory = new FuseJmsListenerContainerFactory(
        maxRetryCount, messageAuditor(), failedMessageConsumer(), isTransientClassifier());

    // This provides all boot's default to this factory, including the message converter
    configurer.configure(listenerFactory, cachingQueueConnFactory);

    // TODO: override any defaults in the listener factory before we return the object


    return listenerFactory;
  }

  @Override
  public void configureJmsListeners(final JmsListenerEndpointRegistrar registrar) {
    registrar.setMessageHandlerMethodFactory(fuseJmsHandlerMethodFactory());
  }

  @Bean
  public MessageHandlerMethodFactory fuseJmsHandlerMethodFactory() {
    DefaultMessageHandlerMethodFactory factory = new FuseMessageHandlerMethodFactory();
    factory.setMessageConverter(jacksonJmsMessageConverter());
    return factory;
  }

  @Bean
  public Predicate<Exception> isTransientClassifier() {
    return err -> ExceptionUtils.indexOfThrowable(err, MessageConversionException.class) == -1;
  }

  @Bean
  public BiConsumer<javax.jms.Message, Exception> failedMessageConsumer() {
    return (msg, err) -> {
      log.error("Failed to process message {} due to error {}", msg, err);
    };
  }

  @Bean
  public BiConsumer<Session, javax.jms.Message> messageAuditor() {
    return (session, msg) -> {
      String dest = "UNKNOWN-DEST";
      String text = "UNKNOWN-BODY";
      try {
        dest = msg.getJMSDestination().toString();
        if (msg instanceof TextMessage) {
          text = ((TextMessage) msg).getText();
        }
      } catch (JMSException jmse) {
        log.error("Could not extract useful info from message.  Using unknown", jmse);
      }
      log.info("Msg arrived from {}|Body:{}|RawMsg:{}", dest, text, msg);
    };
  }

}


