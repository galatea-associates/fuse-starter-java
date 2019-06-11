package org.galatea.starter.service.feign;


import feign.Logger;
import feign.Logger.Level;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

  @Bean
  public Logger.Level feignLoggerLevel() {

    return Level.FULL;
  }

  @Bean
  public ErrorDecoder errorDecoder() {

    return new ErrorDecoder.Default();
  }

}
