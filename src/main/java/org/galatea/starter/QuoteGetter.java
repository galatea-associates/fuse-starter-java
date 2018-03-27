package org.galatea.starter;

import feign.Headers;
import feign.RequestLine;

public interface QuoteGetter {
  @Headers("X-Mashape-Key: o3chJTJnj2mshT5rvZpAZ0BUaiUVp1Ho4XKjsn2JYPspbxkcBk")
  @RequestLine("GET")
  Quote getQuote();

}
