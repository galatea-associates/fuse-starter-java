package org.galatea.starter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.modelresponse.AlphaPrices;
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


  public AlphaPrices getPricesByStock(String stock, String daysToLookBack) {

    long processStartTime = System.currentTimeMillis();

    AlphaPrices objPrices;
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
    objPrices = pricesclient.getPricesByStock(stock, size);
    long processEndTime = System.currentTimeMillis();
    long processTime = processEndTime - processStartTime;
    log.info("Response from Alpha Vantage for stock: {} and the response size: {}. Processing Time was {}, response object: {}.", stock, size, processTime, objPrices);

    return objPrices;

  }

}