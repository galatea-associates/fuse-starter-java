package org.galatea.starter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.MongoDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
@Log
@Service
public class AlphaVantageService {
  @Value("${api.alphavantage}")
  private String key;

  @Autowired
  RestTemplate restTemplate;

  @NonNull
  private ObjectMapper objectMapper;

  /**
   * Serves StockPriceController, handling either the calls to database or request to AlphaVantage.
   * @param symbol String, stock symbol user wants to find the prices of
   * @param days int, the number of days of stock price data to return
   * @return a String, gross mashup of proper JSON {in process of fixing}
   */
  TreeMap<String,MongoDocument> access(final String symbol, final int days) {
    String alphaVantageUrl
        = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol;
    String output = days > 100 ? "full" : "compact";
    String requestUrl = alphaVantageUrl + "&outputsize=" + output + "&apikey=" + key;
    ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);

    assert response.getStatusCode() == HttpStatus.OK; // makes sure we got a clean response
    log.info("Logging the full request url: {}", requestUrl);

    TreeMap<String, MongoDocument> mongoDocumentMap = null;
    //JACKSON TEST BLOCK
    try {
      mongoDocumentMap = mapJsonGraph(objectMapper.readTree(response.getBody()));
    } catch (JsonProcessingException jpe) {
      log.error("Failed to process JSON into MongoDocument in mapJsonGraph().");
    } catch (IOException ioe) {
      log.error("ObjectMapper failed trying to parse JsonTree from response body");
    }
    //JACKSON TEST BLOCK
    log.info("Testing output and @Data annotation of MongoDocuments:\n{}", mongoDocumentMap);

    return mongoDocumentMap;
  }

  private TreeMap<String, MongoDocument> mapJsonGraph(JsonNode root) throws
      JsonProcessingException {
    JsonNode timeSeriesField = root.get("Time Series (Daily)");
    Iterator<String> dates = timeSeriesField.fieldNames();
    TreeMap<String, MongoDocument> tMap = new TreeMap<>();
    while (dates.hasNext()) {
      String date = dates.next();
      JsonNode value = timeSeriesField.get(date);
      MongoDocument md = objectMapper.treeToValue(value, MongoDocument.class);
      md.setDate(date);
      tMap.put(date, md);
    }

    return tMap;
  }
}
