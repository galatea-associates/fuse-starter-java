package org.galatea.starter.entrypoint;

import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller that generates and listens to http endpoints which allow the caller to check
 * for stock prices.
 */
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@RestController
public class StockPriceRestController extends BaseRestController {
  /**
   * Retrieve multiple previously generated Missions.
   */
  // @GetMapping to link http GET requests to this method
  @GetMapping(value = "${webservice.stockPricePath}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public String getStockPrice(){
    return "Hello World";
  }

}
