package org.galatea.starter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.ModelResponse.AlphaPrices;
import org.galatea.starter.service.feign.PricesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Log
@Service

public  class PriceService implements PricesClient {

  /**
   * Process the stock from GET command into the appropriate command
   *
   * @param stock from the full text from the GET command. Wit.ai will break this down
   * @param daysToLookBack from the full text from the GET command. Wit.ai will break this down
   * @return the result of executing the command with the given parameters
   */

  @Autowired
  private PricesClient pricesclient;
  private Object AlphaPrices;
  private Object InternalPrices;
  public static String size;

  @Override
  public AlphaPrices getPricesByStock(String stock, String daysToLookBack) {



    //Determine the response size from Alpha Vantage based on daysToLookBack
    Integer days = Integer.parseInt(daysToLookBack);
    log.info("\n number of days to look back: " + daysToLookBack);
    if (days < 101) {
      size = "compact";
    } else {
      size = "full";
    }

    //Call Alpha Vantage API based on (stock, size)
    AlphaPrices obj_prices = pricesclient.getPricesByStock(stock, size);
    log.info ("\n Response from Alpha Vantage for... \n stock: " + stock + " "
        + "\n response size: " + size +
        "\n Processing time (ms):....... trace-timing-ms"  +
        "\n Response was... \n" + obj_prices );
    return obj_prices;
  }



  //method to return parameters of User's request
  public String processStock(String stock, Integer daysToLookBack) {
    String parameters = stock + " " + daysToLookBack;
    return parameters;
  }
}

