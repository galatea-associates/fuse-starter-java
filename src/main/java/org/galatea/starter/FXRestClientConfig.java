package org.galatea.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import feign.Logger;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import org.galatea.starter.domain.FXRateResponse;
import org.galatea.starter.utils.deserializers.FXRateResponseDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FXRestClientConfig {

    ObjectMapper objectMapper;

    public FXRestClientConfig() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SimpleModule().addDeserializer(FXRateResponse.class, new FXRateResponseDeserializer()));
    }

    // To see the log levels available:
    // https://cloud.spring.io/spring-cloud-netflix/single/spring-cloud-netflix.html#_feign_logging
    // Should probably set this up to be full in test, then limited in prod
    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

    // https://cloud.spring.io/spring-cloud-netflix/single/spring-cloud-netflix.html#spring-cloud-feign-overriding-defaults
    // https://stackoverflow.com/questions/35853908/how-to-set-custom-jackson-objectmapper-with-spring-cloud-netflix-feign
    @Bean
    public Decoder feignDecoder() {
        return new JacksonDecoder(objectMapper);
    }
}
