package org.galatea.starter.entrypoint;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.galatea.starter.service.AlphaVantageService;
import org.galatea.starter.service.PriceRequestService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StockPriceController extends BaseRestController{
  @NonNull
  PriceRequestService priceRequestService;

  @NonNull
  AlphaVantageService alphaVantageService;

  @GetMapping(value = "${webservice.quotepath}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public String quoteEndpoint() {
    return "";
  }
}
