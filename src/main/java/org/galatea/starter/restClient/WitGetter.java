package org.galatea.starter.restClient;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.galatea.starter.domain.wit.WitResponse;

public interface WitGetter {
  @Headers("Authorization: Bearer MMGURXBKQ3YVKYMGDUJQ2K3CKBNMNEVS")
  @RequestLine("GET /message?q={text}")
  WitResponse getWitResponse(@Param("text") String text);

}
