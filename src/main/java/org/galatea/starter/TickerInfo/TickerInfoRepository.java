package org.galatea.starter.TickerInfo;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.lang.Object;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import java.util.SortedMap;
import org.bson.Document;


public class TickerInfoRepository {


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

    private static String getDate(int i){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,i*-1);
        return dateFormat.format(cal.getTime());


    }
}
