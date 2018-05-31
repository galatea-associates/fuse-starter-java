package org.galatea.starter.restclient;
import org.galatea.starter.domain.Quote;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * A FeignCLient that makes Get requests to a webservice that returns a famous quote.
 * Defines a bean that can be used to call getQuote which will return Quote array containing a
 * single quote object. We have to return an array because of the format of the JSON response.
 */
@FeignClient(name="QuoteGetter", url = "${quote-getter.url}")
public interface QuoteGetter {
  @RequestMapping(method = RequestMethod.GET,headers = "X-Mashape-Key=${quote-getter.token}")
  Quote[] getQuote();
}
