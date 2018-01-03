package org.galatea.starter.service.client;

import org.galatea.starter.FXRestClientConfig;
import org.galatea.starter.domain.FXRateResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="fxrates",
    url = "${client.fx-rate}",
    configuration = FXRestClientConfig.class)
public interface IFXRestClient {

    // base = "GBP" would query https://api.fixer.io/latest?base=GBP&symbols=USD
    @RequestMapping(method = RequestMethod.GET,
        value="/latest?base={base}&symbols=USD")
    FXRateResponse getRate(@PathVariable("base") String base);

}
