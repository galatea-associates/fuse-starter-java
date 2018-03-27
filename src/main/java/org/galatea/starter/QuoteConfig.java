package org.galatea.starter;

import feign.Feign;
import feign.gson.GsonDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuoteConfig {
  @Bean
  public QuoteGetter defaultQuoteGetter(){
    return Feign.builder().decoder(new GsonDecoder()).target(QuoteGetter.class, "https://andruxnet-random-famous-quotes.p.mashape.com/?cat=movies");

  }
}
