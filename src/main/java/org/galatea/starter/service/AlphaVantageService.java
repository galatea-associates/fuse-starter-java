package org.galatea.starter.service;

import org.galatea.starter.domain.Ticker;
import org.springframework.web.client.RestTemplate;

/**
 * Communicates with Alpha Vantage to retrieve ticker if not found in repository
 */
public class AlphaVantageService {

  /**
   * @param symbol
   * @return String of Url pointing to the ticker with the given symbol
   */
   private static String getURL(String symbol){
       StringBuilder sb = new StringBuilder("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=");
       sb.append(symbol);
       sb.append("&apikey=90TJ7SQ2CNV9EHMF");
       return sb.toString();
   }

  /**
   * @param symbol
   * @return Ticker with Specified symbol
   */
   public static Ticker getTicker(String symbol){
       RestTemplate restTemplate = new RestTemplate();
       return restTemplate.getForObject(AlphaVantageService.getURL(symbol), Ticker.class);
   }



}
