package org.galatea.starter.entrypoint;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.domain.internal.StockPrices;
import org.galatea.starter.service.PriceService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



/**
 * REST Controller that listens to http endpoints and allows the caller to send text to be
 * processed
 */
@RequiredArgsConstructor
@Slf4j
@Log (enterLevel = Level.INFO, exitLevel = Level.INFO)
@RestController
public class PriceRestController extends BaseRestController {


  @NonNull
  PriceService priceService;

  /**
   * Send the received text to the PriceService to be processed and send the result out
   *
   * @GetMapping to link http GET request to this method
   * @RequestParam to take a parameter from the url
   * @return
   */


  @GetMapping(value = "${webservice.halpath}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public Collection<StockPrices> priceEndpoint(

  @RequestParam(value = "stock") String stock,
      @RequestParam(value = "days", defaultValue= "1", required = false) String daysToLookBack,
      @RequestParam (value = "requestId", required = false) String requestId)

      throws IOException, SQLException {
    processRequestId (requestId);
    return priceService.getPricesByStock(stock, daysToLookBack);
    }
}
