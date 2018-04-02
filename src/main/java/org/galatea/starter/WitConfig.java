package org.galatea.starter;


import feign.Feign;
import feign.gson.GsonDecoder;
import org.galatea.starter.restClient.WitGetter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WitConfig {

  @Bean
  public WitGetter defaultWitGetter(@Value("https://api.wit.ai/message?v=20180328&q=where can I learn about java?") final String witUrl){
    System.out.println("here");
    return Feign.builder().decoder(new GsonDecoder()).target(WitGetter.class, witUrl);
  }
}
