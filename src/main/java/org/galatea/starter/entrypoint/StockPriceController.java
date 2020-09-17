package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.MongoDocument;
import org.galatea.starter.service.PriceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
public class StockPriceController extends BaseRestController {
  @Autowired
  PriceRequestService priceRequestService;

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

    TreeMap<String, MongoDocument> processed = priceRequestService.access(ticker, days);
    log.info("Returned from PriceRequestService with map of MongoDocuments.");
    assert processed != null && !processed.isEmpty(); //this might not be assertable in the future
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
      objectNode.putPOJO("prices (USD)", processed.get(key)); // converts MongoDoc to JSON
      jsonArrayRoot.add(objectNode);
      key = processed.lowerKey(key);
    }
    try {
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    } catch (JsonProcessingException jpe) {
      log.error("Failed in pretty-printing Json tree with ObjectMapper.");
    }

    return "{ \"sorry\" : \"Something failed internally. Please try again.\" }";
  }
}
