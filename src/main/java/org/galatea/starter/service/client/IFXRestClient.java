package org.galatea.starter.service.client;

import org.galatea.starter.FXRestClientConfig;
import org.galatea.starter.domain.FXRateResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

@FeignClient(name="fxrates",
    url = "http://api.fixer.io",
    configuration = FXRestClientConfig.class)
public interface IFXRestClient {

    // base = "GBP" would query https://api.fixer.io/latest?base=GBP&symbols=USD
    // which returns something like {"base":"GBP","date":"2017-11-30","rates":{"USD":1.3467}}
    @RequestMapping(method = RequestMethod.GET,
        value="/latest?base={base}&symbols=USD")
    FXRateResponse getRate(@PathVariable("base") String base);

}
