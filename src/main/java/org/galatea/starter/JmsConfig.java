
package org.galatea.starter;

import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.utils.FuseTraceRepository;
import org.galatea.starter.utils.jms.FuseJmsListenerContainerFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
@Slf4j
public class JmsConfig implements JmsListenerConfigurer {

  @Bean
  public MessageConverter jacksonJmsMessageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    return converter;
  }

  /**
   * We provide our own listener container factory since we want to use our own implementation of a
   * listener container which adds tracing of how the message is handled.
   *
   * @param queueConnectionFactory injected by spring
   * @param configurer injected by spring
   * @param tracerRpsy injected by spring
   * @return the factory.
   */
  @Bean
  public JmsListenerContainerFactory<?> jmsListenerContainerFactory(
      final ConnectionFactory queueConnectionFactory,
      final DefaultJmsListenerContainerFactoryConfigurer configurer,
      final FuseTraceRepository tracerRpsy) {

    FuseJmsListenerContainerFactory listenerFactory =
        new FuseJmsListenerContainerFactory(tracerRpsy);

    // This provides all boot's default to this factory, including the message converter
    // Note that we don't use a caching connection factory due to this:
    // http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jms/listener/DefaultMessageListenerContainer.html
    configurer.configure(listenerFactory, queueConnectionFactory);

    // TODO: override any defaults in the listener factory before we return the object

    return listenerFactory;
  }


  /**
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

  @Override
  public void configureJmsListeners(final JmsListenerEndpointRegistrar registrar) {
    registrar.setMessageHandlerMethodFactory(jmsHandlerMethodFactory());
  }
}


