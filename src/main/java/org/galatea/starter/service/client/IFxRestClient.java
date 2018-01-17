package org.galatea.starter.service.client;

import org.galatea.starter.FxRestClientConfig;
import org.galatea.starter.domain.FxRateResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@FeignClient(name = "fxrates", url = "${client.fx-rate}", configuration = FxRestClientConfig.class)
public interface IFxRestClient {

  // base = "GBP" would query http://api.fixer.io/latest?base=GBP&symbols=USD
  @Valid
  @RequestMapping(method = RequestMethod.GET, value = "/latest?base={base}&symbols=USD")
  FxRateResponse getRate(@PathVariable("base") String base);
}
