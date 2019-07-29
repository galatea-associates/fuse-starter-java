package org.galatea.starter.TickerInfo;


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

import org.springframework.web.client.RestTemplate;


public class TickerInfoRepository {


    public static void main(String [] args){

        //Obtain MongoDB Collection
        MongoClientURI uri = new MongoClientURI("mongodb+srv://ReneBorr:GalaPassword@tickerinfo-glh7c.mongodb.net/admin?authSource=admin");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("AlphaVantage");
        MongoCollection col = database.getCollection("Tickers");
        //rest Template
        RestTemplate restTemplate = new RestTemplate();
        //InfoRepository
        TickerInfoRepository repo = new TickerInfoRepository();


        Ticker ticker = repo.getTicker("AAPL",20);

        //col.insertOne(ticker);
    }


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
