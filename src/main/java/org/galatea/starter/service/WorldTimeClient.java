package org.galatea.starter.service;

import org.galatea.starter.domain.WorldTimeApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Declarative REST Client to access endpoints from World Time's free open API. See
 * Documentation: http://worldtimeapi.org
 */
@FeignClient(name = "WorldTime", url = "${spring.rest.worldTimeBasePath}")
public interface WorldTimeClient {

  /**
   * Query World Time API to get the current time for the given timezone.
   *
   * @param timezone the timezone to get the current time for.
   * @return WitAiQueryResponse indicating the meaning of the given query.
   */
  @GetMapping("/timezone/{timezone}")
  WorldTimeApiResponse getCurrentTimeForTimezone(@PathVariable("timezone") String timezone);

}
