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
  public WitGetter defaultWitGetter(@Value("https://api.wit.ai") final String witUrl){
    return Feign.builder().decoder(new GsonDecoder()).target(WitGetter.class, witUrl);
  }
}
