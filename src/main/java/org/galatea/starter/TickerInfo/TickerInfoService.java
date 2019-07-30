package org.galatea.starter.TickerInfo;


import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.lang.Object;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import java.util.SortedMap;
import org.bson.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Component
public class TickerInfoService {




  public Ticker getTicker(String ticker, int days){
    Ticker info;

    //Check if Available in Database
    if(false){
      //Fill in mongo Access code here
    }
    else{
      info = AlphaVantageService.getTicker(ticker);





    }
    trimTicker(info,days);
    return info;
  }

  private void trimTicker(Ticker ticker, int days){
    SortedMap<String, Day> timeSeries = new TreeMap<String,Day>(Collections.reverseOrder());
    for(int i = 0; i<days; i++) {
      if(ticker.timeSeries.get(getDate(i)) != null) {
        timeSeries.put(getDate(i), ticker.timeSeries.get(getDate(i)));
      }
    }
    ticker.setTimeSeries(timeSeries);
  }

  private String getDate(int i){
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_YEAR,i*-1);
    return dateFormat.format(cal.getTime());
  }
}