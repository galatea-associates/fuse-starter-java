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
public class StockPriceController extends BaseRestController{
  @NonNull
  PriceRequestService priceRequestService;

  @NonNull
  AlphaVantageService alphaVantageService;
  @GetMapping(value ="${webservice.quotepath}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public String quoteEndpoint(@RequestParam(value="ticker") String ticker,
      @RequestParam(value="days") int days) {
    if (days < 1) {
      throw new IllegalArgumentException(
          String.format("'days' parameter should be greater than 1. Was %d", days));
    }

    TreeMap<String, MongoDocument> processed = alphaVantageService.access(ticker, days);
    StringBuilder sb = new StringBuilder();
    JsonObject response = new JsonObject();

    sb.append("output size: ");
    sb.append(processed.size());
    sb.append(System.getProperty("line.separator"));
    String key = processed.lastKey();
    JsonObject entry = new JsonObject();
    MongoDocument value = processed.get(key);
    entry.addProperty("open", value.getOpen());
    entry.addProperty("high", value.getHigh());
    entry.addProperty("low", value.getLow());
    entry.addProperty("close", value.getClose());
    entry.addProperty("volume", value.getVolume());
    response.add(key, entry);
    sb.append(key)
        .append(":")
        .append(System.getProperty("line.separator"))
        .append(value)
        .append(System.getProperty("line.separator"));
    while ((key = processed.lowerKey(key)) != null) {
      entry = new JsonObject();
      entry.addProperty("open", value.getOpen());
      entry.addProperty("high", value.getHigh());
      entry.addProperty("low", value.getLow());
      entry.addProperty("close", value.getClose());
      entry.addProperty("volume", value.getVolume());
      response.add(key, entry);
    }
    return response.getAsString();
  }
}
