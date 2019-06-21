package org.galatea.starter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.internal.FullResponse;
import org.galatea.starter.domain.internal.StockMetadata;
import org.galatea.starter.domain.internal.StockMetadata.StockMetadataBuilder;
import org.galatea.starter.domain.internal.StockPrices;
import org.galatea.starter.domain.internal.StockPrices.StockPricesBuilder;
import org.galatea.starter.domain.modelresponse.AlphaPrices;
import org.galatea.starter.domain.modelresponse.ResponsePrices;
import org.galatea.starter.service.feign.PricesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    ArrayList<StockPrices> convertedPrices = ConvertPrices(stock, objPrices);

    //Filter response size, starting with T=1
    Collections.sort(convertedPrices, Comparator.comparing(StockPrices::getDate).reversed());
    filteredPrices = convertedPrices.subList(1, days);

    log.info("Response from Alpha Vantage for stock: {} and the response size: {}. Processing Time was {}, response object: {}.", stock, size, processTime, filteredPrices);
    return filteredPrices;
  }


  public ArrayList<StockPrices> ConvertPrices(String stock, AlphaPrices objPrices) {

    StockPrices dataPoints;
    ArrayList<StockPrices> converted = new ArrayList<>();


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

  public String BuildMeta(String stock, String days)
      throws UnknownHostException, JsonProcessingException {

    StockMetadata stockMetadata;
    ArrayList<StockMetadata> metadata = new ArrayList<>();

    Long processEndTime = System.currentTimeMillis();
    processTime = processEndTime - processStartTime;

    Long process = processTime;
    String processTime = process.toString();

    Long startTime = processStartTime;
    String start = DateFormat.getInstance().format(startTime);


    //Construct Meta Data object
    StockMetadataBuilder builder = StockMetadata.builder();
    builder.endpoint("price?stock=" + stock + "&days=" + days);
    builder.host(InetAddress.getLocalHost().getHostName());
    builder.responseTime(processTime + "(ms)");
    builder.timeStamp(start);
    stockMetadata = builder.build();

    metadata.add(stockMetadata);


    // Create complete response object and pretty print JSON
    FullResponse fullResponse = new FullResponse(metadata, filteredPrices);
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    String json = mapper.writeValueAsString(fullResponse);
    return json;
  }
}
