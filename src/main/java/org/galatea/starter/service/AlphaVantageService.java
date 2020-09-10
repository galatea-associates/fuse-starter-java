package org.galatea.starter.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.MongoDocument;
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

  @NonNull
  private Gson gson;

  public TreeMap<String,MongoDocument> access(String symbol, int days) {
    RestTemplate restTemplate = new RestTemplate();
    String alphaVantageUrl
        = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol;
    String output = days > 100 ? "full":"compact";
    String requestUrl = alphaVantageUrl + "&outputsize=" + output + "&apikey=" + key;
    ResponseEntity<String> response = restTemplate
        .getForEntity(requestUrl, String.class);
    //
    assert response.getStatusCode() == HttpStatus.OK; // makes sure we got a clean response
    log.info("Logging the full request url: {}", requestUrl);

    Type dailyStock = new TypeToken<TreeMap<String, MongoDocument>>(){}.getType();

    JsonParser jsonParser = new JsonParser();
    JsonElement jsonElement = jsonParser.parse(response.getBody());
    log.info("Confirming that GSON is properly parsing the response body: {}", jsonElement.toString());
    JsonObject weeklyTimeSeries = jsonElement.getAsJsonObject()
        .getAsJsonObject("Time Series (Daily)"); //grabs member of the root JSON object
    assert weeklyTimeSeries != null; //is this causing trouble?
    log.info("Logging contents of 'weeklyTimeSeries' JsonObject: {}", weeklyTimeSeries);
    TreeMap<String, MongoDocument> mongoDocuments = gson.fromJson(weeklyTimeSeries, dailyStock);
    //maps each element in the array of JSON Daily Reports to 'MongoDocument' POJO

    log.info("Testing output and @Data annotation of MongoDocuments:\n{}", mongoDocuments);

    return mongoDocuments;
  }
}
