package org.galatea.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import feign.Logger;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;

import org.galatea.starter.domain.FxRateResponse;
import org.galatea.starter.utils.deserializers.FxRateResponseDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FxRestClientConfig {

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  /**
   * Need a custom deserializer as FxRateResponse has non-primitive types. Have to attach
   * FxRateResponseDeserializer to JacksonDecoder via an ObjectMapper.
   */
  @Bean
  public Decoder feignDecoder() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(
        new SimpleModule().addDeserializer(FxRateResponse.class, new FxRateResponseDeserializer()));
    return new JacksonDecoder(objectMapper);
  }
}
