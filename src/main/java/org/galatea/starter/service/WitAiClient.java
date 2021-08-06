package org.galatea.starter.service;

import org.galatea.starter.domain.WitAiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign Declarative REST Client to access endpoints from Facebook's Wit.ai NLP API. See
 * Documentation: https://wit.ai/docs/http/
 */
@FeignClient(name = "WitAi", url = "${spring.rest.witAiBasePath}")
public interface WitAiClient {

  /**
   * Return the meaning of a sentence from Facebook's Wit.ai NLP. See
   * https://wit.ai/docs/http/20210928/#get__message_link
   *
   * @param bearerToken Authorization header value
   * @param version version of API. See https://wit.ai/docs/http/20210928/#api_versioning_link.
   * @param query the sentence to query for.
   * @return WitAiQueryResponse indicating the meaning of the given query.
   */
  @GetMapping("/message")
  WitAiResponse query(
      @RequestHeader("Authorization") String bearerToken,
      @RequestParam("v") String version,
      @RequestParam("q") String query);

}
