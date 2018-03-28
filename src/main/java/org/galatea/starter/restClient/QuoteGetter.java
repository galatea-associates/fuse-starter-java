package org.galatea.starter.restClient;

import feign.Headers;
import feign.RequestLine;
import org.galatea.starter.domain.Quote;

public interface QuoteGetter {
  @Headers("X-Mashape-Key: o3chJTJnj2mshT5rvZpAZ0BUaiUVp1Ho4XKjsn2JYPspbxkcBk")
  @RequestLine("GET")
  Quote getQuote();
}
