package org.galatea.starter.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.internal.StockMetadata;
import org.galatea.starter.domain.internal.StockMetadata.StockMetadataBuilder;
import org.galatea.starter.domain.internal.StockPrices;
import org.galatea.starter.domain.internal.StockPrices.StockPricesBuilder;
import org.galatea.starter.domain.modelresponse.AlphaPrices;
import org.galatea.starter.domain.modelresponse.ResponsePrices;
import org.galatea.starter.service.feign.PricesClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import org.galatea.starter.domain.internal.InternalPrices;

@RequiredArgsConstructor
@Slf4j
@Log
@Service


public  class PriceService {

  /**
   * Process the stock from GET command into the appropriate command
   *
   * @param stock from the full text from the GET command. Wit.ai will break this down
   * @param daysToLookBack from the full text from the GET command. Wit.ai will break this down
   * @return the result of executing the command with the given parameters
   */


  @Autowired
  private PricesClient pricesclient;
  public long processTime;
  public long processStartTime;
  public Collection<StockPrices> filteredPrices;

  public Collection<StockPrices> getPricesByStock(String stock, String daysToLookBack) {

    processStartTime = System.currentTimeMillis();
    String size;

    //Determine the response size from Alpha Vantage based on daysToLookBack
    Integer days = Integer.parseInt(daysToLookBack);
    log.info("\n number of days to look back: " + daysToLookBack);
    if (days < 101) {
      size = "compact";
    } else {
      size = "full";
    }

    //Call Alpha Vantage API based on (stock, size)
    AlphaPrices objPrices = pricesclient.getPricesByStock(stock, size);

    //Convert to internal Price Objects

    //sort response to grabe first 10 days
    ArrayList<StockPrices> convertedPrices = ConvertPrices(stock, objPrices);

    //Filter response size, starting with T=1
    filteredPrices = convertedPrices.subList(1, days);
    long processEndTime = System.currentTimeMillis();
    processTime = processEndTime - processStartTime;
    log.info("Response from Alpha Vantage for stock: {} and the response size: {}. Processing Time was {}, response object: {}.", stock, size, processTime, filteredPrices);
    return filteredPrices;
  }


  public ArrayList<StockPrices> ConvertPrices(String stock, AlphaPrices objPrices) {

    StockPrices dataPoints;
    ArrayList<StockPrices> converted = new ArrayList<StockPrices>();


    // Convert Alpha Vantage prices to InternalPrices object
    for (Entry<Date, ResponsePrices> entry : objPrices.getAvPrices()
        .entrySet()) {

      Date dates = entry.getKey();
      ResponsePrices internalPrices = entry.getValue();
      StockPricesBuilder builder = StockPrices.builder();
      builder.date(dates);
      builder.adjustedClose(internalPrices.getAdjustedClose());
      builder.close(internalPrices.getClose());
      builder.high(internalPrices.getHigh());
      builder.close(internalPrices.getClose());
      builder.volume(internalPrices.getVolume());
      builder.splitCoefficient(internalPrices.getSplitCoefficient());
      builder.low(internalPrices.getLow());
      builder.stock(stock);
      dataPoints = builder.build();

      converted.add(dataPoints);
    }
    return converted;
  }

  public JSONObject BuildMeta(String stock, String days)
      throws UnknownHostException {

    StockMetadata stockMetadata;
    ArrayList<StockMetadata> metadata = new ArrayList<>();
    Long process = processTime;
    int processTime = process.intValue();
    Long startTime = processStartTime;
    int start = startTime.intValue();



    StockMetadataBuilder builder = StockMetadata.builder();
    builder.endpoint("price?stock=" + stock + "&days=" + days);
    builder.host(InetAddress.getLocalHost().getHostName());
    builder.responseTime(processTime);
    builder.timeStamp(start);
    stockMetadata = builder.build();

    metadata.add(stockMetadata);
    log.info ("Meta Data: {}", metadata);
    System.out.println(metadata);

    JSONObject fullResponse = new JSONObject();
    fullResponse.put("MetaData", stockMetadata);
    fullResponse.put("Stock Prices", filteredPrices);

    System.out.println(fullResponse);
    return fullResponse;
  }
}
