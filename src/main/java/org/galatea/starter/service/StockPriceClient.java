package org.galatea.starter.service;

import java.util.List;
import org.galatea.starter.domain.AlphaVantageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A Feign Declarative REST Client to access endpoints from the Free and Open Alpha Vantage API to get
 * daily stock time series. See https://www.alphavantage.co/documentation/
 */

@FeignClient(name = "AlphaVantage", url = "${spring.rest.alphaVantageBasePath}")

public interface StockPriceClient {

  /**
   * Get the list of prices for the stock symbol passed in for the specified number of days given.
   * See https://www.alphavantage.co/documentation/#daily.
   *
   * @param symbol stock symbols to get last traded price for.
   * @return a list of the prices for each of the last N days for the symbol passed in.
   */

  @GetMapping("function=TIME_SERIES_DAILY")
  AlphaVantageResponse getPricesForSymbolForLastNDays(@RequestParam("symbol") String symbol, @RequestParam("days") Integer days);

}
