package org.galatea.starter;

import feign.Feign;
import feign.gson.GsonDecoder;
import org.galatea.starter.restclient.QuoteGetter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuoteConfig {

  @Bean
  public QuoteGetter defaultQuoteGetter(@Value("${quote-getter.url}") final String quoteUrl){
    return Feign.builder().decoder(new GsonDecoder()).target(QuoteGetter.class, quoteUrl);

  }
}
