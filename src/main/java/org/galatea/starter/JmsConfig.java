package org.galatea.starter;

import java.util.function.BiConsumer;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.utils.jms.FuseJmsListenerContainerFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@Slf4j
@Configuration
@EnableJms
public class JmsConfig implements JmsListenerConfigurer {

  /**
   * Returns an implementation of failed message consumer that simply logs the message.
   */
  @Bean
  public BiConsumer<Message, Exception> failedMessageConsumer() {
    return (msg, err) -> log.error(
        "Message {} failed to process after retries.  Removing message from queue", msg, err);
  }

  /**
   * Returns a message converter to handle JSON formatted messages.
   */
  @Bean
  public MessageConverter jacksonJmsMessageConverter() {
    return new MappingJackson2MessageConverter();
  }

  /**
   * We provide our own listener container factory since we want to use our own implementation of a
   * listener container which adds tracing of how the message is handled. We also manually set the
   * message converter to ensure that it is using the correct message format.
   *
   * @param queueConnectionFactory injected by spring
   * @param configurer injected by spring
   * @return the factory.
   */
  @Bean
  public JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsListenerContainerFactory(
      final ConnectionFactory queueConnectionFactory,
      final DefaultJmsListenerContainerFactoryConfigurer configurer,
      final BiConsumer<Message, Exception> failedMessageConsumer) {

    FuseJmsListenerContainerFactory listenerFactory =
        new FuseJmsListenerContainerFactory(failedMessageConsumer);

    // This provides all boot's default to this factory, including the message converter
    // Note that we don't use a caching connection factory due to this:
    //
    // http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/
    // jms/listener/DefaultMessageListenerContainer.html
    configurer.configure(listenerFactory, queueConnectionFactory);

    // TODO: override any defaults in the listener factory before we return the object
    return listenerFactory;
  }

  /**
   * For JSON messages we want to use the spring messaging converter instead of the spring jms
   * converter. The spring jms converter expects the type of object to deserialize the json to being
   * specified in the message itself, while the spring messaging converter will do what you expect
   * and convert to the type of the input parameter in the listener.
   *
   * <p>In order to use a spring messaging converter we have to implement JmsListenerConfigurer and
   * set the custom MessageHandlerMethodFactory.
   *
   * @return a new handler factory that uses a different message converter than the default one.
   */
  @Bean
  public MessageHandlerMethodFactory jmsHandlerMethodFactory() {
    DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();

    // Note that we use the spring messaging converter instead of the spring jms converter. The two
    // behave differently.
    factory.setMessageConverter(jacksonJmsMessageConverter());
    return factory;
  }

  /**
   * This sets the custom MessageHandlerMethodFactory for the listener registrar for the connection
   * factory that we've set up for JSON.
   */
  @Override
  public void configureJmsListeners(final JmsListenerEndpointRegistrar registrar) {
    registrar.setMessageHandlerMethodFactory(jmsHandlerMethodFactory());
  }
}


