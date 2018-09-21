package org.galatea.starter.restclient;

import org.galatea.starter.domain.wit.WitResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * A FeignClient that makes GET requests to Wit.ai for natural language processing
 * Defines a bean that can be used to call getWitResponse which takes a Bearer token and a
 * plain english expression. This expression is sent to Wit.ai which tries to extract and return meaning
 */
@FeignClient(name="WitGetter", url = "${wit.url}")
public interface WitGetter {
  @RequestMapping(method = RequestMethod.GET, value = "/message?v=20180509&q={text}", consumes = "application/json")

  WitResponse getWitResponse(@RequestHeader("Authorization") final String bearerToken, @PathVariable("text") final String text);
}
