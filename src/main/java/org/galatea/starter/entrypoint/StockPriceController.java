package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    // constructing the array of 'days'-limited stock price results
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode root = objectMapper.createObjectNode();
    int iterations = days > processed.size() ? processed.size() : days;
    ArrayNode jsonArrayRoot = root.putArray(String.format("%s Daily Stock Prices (%d)",
        ticker.toUpperCase(), iterations));
    String key = processed.lastKey();
    // serializes each entry as a JSON object, up to iterations
    for (int i = 0; i < iterations; i++) {
      ObjectNode objectNode = objectMapper.createObjectNode();
      objectNode.put("date", key);
      objectNode.putPOJO("prices", processed.get(key)); // converts MongoDoc to JSON
      jsonArrayRoot.add(objectNode);
      key = processed.lowerKey(key);
    }
    try {
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    } catch (JsonProcessingException jpe) {
      //too lazy to add logger for this part.
    }

    return "{ \"sorry\" : \"Something failed internally. Please try again.\" }";

  }
}
