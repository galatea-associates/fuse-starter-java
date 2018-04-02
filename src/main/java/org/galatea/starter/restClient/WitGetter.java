package org.galatea.starter.restClient;

import feign.Headers;
import feign.RequestLine;
import org.galatea.starter.domain.Wit.WitResponse;

public interface WitGetter {
  @Headers("Authorization: Bearer RZMD4U2WQB4ZYBUEKJ3RASFG3GIW5NKN")
  @RequestLine("GET")
  WitResponse getWitResponse();

}
