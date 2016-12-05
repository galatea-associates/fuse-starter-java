
package org.galatea.starter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {

  // The number of concurrent threads that can pull messages off the queue
  @Value("${jms.listener-concurrency}")
  protected String listenerConcurrency;


  @Bean
  public MessageConverter jacksonJmsMessageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    return converter;
  }

  @Bean
  public JmsListenerContainerFactory<?> jmsListenerContainerFactory(
      final ConnectionFactory queueConnectionFactory,
      final DefaultJmsListenerContainerFactoryConfigurer configurer) {

    CachingConnectionFactory cachingQueueConnFactory =
        new CachingConnectionFactory(queueConnectionFactory);
    DefaultJmsListenerContainerFactory listenerFactory = new DefaultJmsListenerContainerFactory();

    // This provides all boot's default to this factory, including the message converter
    configurer.configure(listenerFactory, cachingQueueConnFactory);

    // TODO: override any defaults in the listener factory before we return the object


    return listenerFactory;
  }

}
