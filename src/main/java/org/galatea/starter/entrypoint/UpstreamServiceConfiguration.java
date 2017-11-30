package org.galatea.starter.entrypoint;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpstreamServiceConfiguration {

    // To see the log levels available:
    // https://cloud.spring.io/spring-cloud-netflix/single/spring-cloud-netflix.html#_feign_logging
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

}
