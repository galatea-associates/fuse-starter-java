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

  ObjectMapper objectMapper;

  /** Create ObjectMapper for use in JacksonDecoder. */
  public FxRestClientConfig() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(
        new SimpleModule().addDeserializer(FxRateResponse.class, new FxRateResponseDeserializer()));
  }

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  @Bean
  public Decoder feignDecoder() {
    return new JacksonDecoder(objectMapper);
  }
}
