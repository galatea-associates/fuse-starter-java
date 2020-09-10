package org.galatea.starter.entrypoint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.galatea.starter.domain.MongoDocument;
import org.galatea.starter.service.AlphaVantageService;
import org.galatea.starter.service.PriceRequestService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StockPriceController extends BaseRestController {
  @NonNull
  PriceRequestService priceRequestService;

  @NonNull
  AlphaVantageService alphaVantageService;

  /**
   * Pulls 'ticker' and 'days' params from GET request to '/prices' entrypoint and pass along
   * to alphavantge API.
   * @param ticker String, stock symbol user wants to find the prices of
   * @param days int, the number of days of stock price data to return
   * @return String, JSON repr of the data requested
   */
  @GetMapping(value = "${webservice.quotepath}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public String quoteEndpoint(@RequestParam(value = "ticker") final String ticker,
      @RequestParam(value = "days") final int days) {
    if (days < 1) {
      throw new IllegalArgumentException(
          String.format("'days' parameter should be greater than 1. Was %d", days));
    }

    TreeMap<String, MongoDocument> processed = alphaVantageService.access(ticker, days);
    return processed.descendingMap().toString();
  }
}
