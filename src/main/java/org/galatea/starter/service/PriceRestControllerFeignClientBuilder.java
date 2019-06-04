package org.galatea.starter.service;


import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.galatea.starter.domain.StockPrice;

public class PriceRestControllerFeignClientBuilder {

  private StockPrice stockPrice = createClient(StockPrice.class, "http://localhost:8080/prices");

  private static <T> StockPrice createClient(Class<T> type, String uri) {
    return Feign.builder()
        .client(new OkHttpClient())
        .encoder(new JacksonEncoder())
        .decoder(new JacksonDecoder())
        .logger(new Slf4jLogger(StockPrice.class))
        .logLevel(Logger.Level.FULL)
        .target(StockPrice.class, "http://localhost:8080/prices");
  }
}
