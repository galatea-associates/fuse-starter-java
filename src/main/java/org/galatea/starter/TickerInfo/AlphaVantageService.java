package org.galatea.starter.TickerInfo;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;



public class AlphaVantageService {

   private static String getURL(String ticker){
       StringBuilder sb = new StringBuilder("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=");
       sb.append(ticker);
       sb.append("&apikey=90TJ7SQ2CNV9EHMF");
       return sb.toString();
   }
   public static Ticker getTicker(String symbol){
       RestTemplate restTemplate = new RestTemplate();
       Ticker tickerdata = restTemplate.getForObject(AlphaVantageService.getURL(symbol), Ticker.class);
       return tickerdata;
   }



}
