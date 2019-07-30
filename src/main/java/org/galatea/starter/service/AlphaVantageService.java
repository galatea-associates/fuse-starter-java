package org.galatea.starter.service;

import org.galatea.starter.domain.Ticker;
import org.springframework.web.client.RestTemplate;


public class AlphaVantageService {

   private static String getURL(String ticker){
       StringBuilder sb = new StringBuilder("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=");
       sb.append(ticker);
       sb.append("&apikey=90TJ7SQ2CNV9EHMF");
       return sb.toString();
   }
   public static Ticker getTicker(String symbol){
       RestTemplate restTemplate = new RestTemplate();
       return restTemplate.getForObject(AlphaVantageService.getURL(symbol), Ticker.class);
   }



}
