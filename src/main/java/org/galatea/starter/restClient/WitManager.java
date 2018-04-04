package org.galatea.starter.restClient;

import feign.Headers;
import feign.RequestLine;

public interface WitManager {
  @Headers("Authorization: Bearer RZMD4U2WQB4ZYBUEKJ3RASFG3GIW5NKN")
  @RequestLine("POST")
  public void suckadick();

}
