package org.galatea.starter.restclient;

import com.netflix.ribbon.proxy.annotation.Http.Header;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.galatea.starter.domain.wit.WitResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name="WitGetter", url = "${wit.url}")
public interface WitGetter {
  @RequestMapping(method = RequestMethod.GET, value = "/message?v=20180509&q={text}", consumes = "application/json")
  WitResponse getWitResponse(@RequestHeader("Authorization") final String bearerToken, @PathVariable("text") final String text);
}
