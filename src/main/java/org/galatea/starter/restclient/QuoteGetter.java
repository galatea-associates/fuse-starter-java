package org.galatea.starter.restclient;
import org.galatea.starter.domain.Quote;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name="QuoteGetter", url = "${quote-getter.url}")
public interface QuoteGetter {
  @RequestMapping(method = RequestMethod.GET,headers = "X-Mashape-Key=${quote-getter.token}")
  Quote getQuote();
}
