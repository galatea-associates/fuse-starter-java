package org.galatea.starter.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.internal.StockPrices;
import org.galatea.starter.domain.internal.StockPrices.StockPricesBuilder;
import org.galatea.starter.domain.modelresponse.AlphaPrices;
import org.galatea.starter.domain.modelresponse.ResponsePrices;
import org.galatea.starter.service.feign.PricesClient;
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
  private AlphaPrices obj_prices;
  static ArrayList<StockPrices> converted = new ArrayList<StockPrices>();



  public AlphaPrices getPricesByStock(String stock, String daysToLookBack) {

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
    obj_prices = pricesclient.getPricesByStock(stock, size);
    log.info("\n Response from Alpha Vantage for... \n stock: " + stock + " "
        + "\n response size: " + size +
        "\n Processing time (ms):....... trace-timing-ms" +
        "\n Response was... \n" + obj_prices);

    return obj_prices;

  }


  // Convert Alpha Vantage prices to InternalPrices object
  public Collection<StockPrices> ConvertPrices(String stock) {

    for (Entry<Date, ResponsePrices> entry : obj_prices.getAvPrices()
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
      StockPrices dataPoints = builder
          .build();

      converted.add(dataPoints);
    }
    return converted;
  }
}

