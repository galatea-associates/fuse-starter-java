package org.galatea.starter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapIterator;
import org.galatea.starter.domain.AVDay;
import org.galatea.starter.domain.AVStock;
import org.galatea.starter.domain.AVTimeSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * A layer for transformation, aggregation, and business required when retrieving data from AlphaVantage.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PriceFinderService {

  final static String AVQueryURL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&";
  final static String AVApikey = "XNRUM1DDXGFTDL82";

  @NonNull
  private ObjectMapper objectMapper;
  @NonNull
  private RestTemplate restTemplate;

/**
 * Get all available data regarding requested stock from AlphaVantage and requested N days
 *
 * @return A ResponseEntity with a body of java objects containing N days price information for the
 * requested stock
 */
  public ResponseEntity<AVStock> getPriceInformation(final String text, int numberOfDays) {
    String AVGetRequest = (new StringBuilder()).append(AVQueryURL).append("symbol=")
        .append(text).append("&apikey=").append(AVApikey).toString();
    ResponseEntity<AVStock> data = restTemplate.getForEntity(AVGetRequest, AVStock.class);
    AVStock nDaysOfData = getNDays(data.getBody(), numberOfDays);
    ResponseEntity dataDesiredFormat = data.ok(nDaysOfData.getAVTimeSeries());
    return dataDesiredFormat;
  }


  public AVStock getNDays (AVStock allData, int numberOfDays) {
    HashMap<String, AVDay> nDaysOfData = new HashMap<String, AVDay>();
    AVStock result = new AVStock();
    Iterator<Map.Entry<String, AVDay>> entries = allData.getAVTimeSeries().entrySet().iterator();
    while (entries.hasNext() && numberOfDays > 0) {
      Map.Entry<String, AVDay> entry = entries.next();
      nDaysOfData.put(entry.getKey(), entry.getValue());
      numberOfDays -= 1;
    }
    result.setAVTimeSeries(nDaysOfData);
    return result;
  }
}
