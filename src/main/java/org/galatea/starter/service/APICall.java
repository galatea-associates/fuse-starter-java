package org.galatea.starter.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;


public class APICall {
  public static void main(String[] args) {
    try {
      APICall.call_me();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static void call_me () throws Exception {
    String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=TSLA&outputsize=compact&apikey=Q4XJ9KJWS5A109C6";
    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    // optional default is GET
    con.setRequestMethod("GET");
//    //add request header
//    con.setRequestProperty("User-Agent", "Mozilla/5.0");
    int responseCode = con.getResponseCode();
    System.out.println("\nSending 'GET' request to URL : " + url);
    System.out.println("\nResponse Code : " + responseCode);
    BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    //print in String
    System.out.println(response.toString());

    //Read JSON response and print
    JSONObject myResponse = new JSONObject(response.toString());
    System.out.println("\nresult after Reading JSON Response");
    System.out.println(myResponse);

  }
}