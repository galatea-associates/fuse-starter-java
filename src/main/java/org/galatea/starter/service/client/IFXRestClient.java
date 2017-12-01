package org.galatea.starter.service.client;

import org.galatea.starter.FXRestClientConfig;
import org.galatea.starter.domain.FXRateResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="fxrates",
    url = "http://api.fixer.io",
    configuration = FXRestClientConfig.class)
public interface IFXRestClient {

    // base = "GBP" would query https://api.fixer.io/latest?base=GBP&symbols=USD
    // which returns {"base":"GBP","date":"2017-11-30","rates":{"USD":1.3467}}
    @RequestMapping(method = RequestMethod.GET,
        value="/latest?base={base}&symbols=USD")
    FXRateResponse rate(@RequestParam("base") String base);

}
