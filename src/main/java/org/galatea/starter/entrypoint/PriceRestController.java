package org.galatea.starter.entrypoint;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Collection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.domain.internal.FullResponse;
import org.galatea.starter.domain.internal.StockMetadata;
import org.galatea.starter.domain.internal.StockMetadata.StockMetadataBuilder;
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
  long totalTime;
  String start;
  Collection<StockPrices> filtered;

  /**
   * Send the received text to the PriceService to be processed and send the result out
   *
   * @GetMapping to link http GET request to this method
   * @RequestParam to take a parameter from the url
   * @return
   */


  @GetMapping(value = "${webservice.pricepath}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public FullResponse priceEndpoint(

  @RequestParam(value = "stock") String stock,
      @RequestParam(value = "days", defaultValue= "2", required = false) String daysToLookBack,
      @RequestParam (value = "requestId", required = false) String requestId)

      throws IOException{

    //Start total application process time
    long processStartTime = System.currentTimeMillis();
    start = DateFormat.getInstance().format(processStartTime);
    processRequestId (requestId);
    stock = stock.toUpperCase();

    //Start time for calling Alpha Vantage
    long responseStartTime = System.currentTimeMillis();
    filtered = priceService.getPricesByStock(stock, daysToLookBack);

    long responseEndTime = System.currentTimeMillis();
    long timeToCallAlphaVantage = responseEndTime - responseStartTime;


    long processEndTime = System.currentTimeMillis();
    totalTime = processEndTime - processStartTime;
    return buildFullResponse(stock, daysToLookBack, timeToCallAlphaVantage);
  }

  public FullResponse buildFullResponse(String stock, String days, Long responseTime)
      throws UnknownHostException{

    StockMetadata stockMetadata;

    //Construct Meta Data object
    StockMetadataBuilder builder = StockMetadata.builder();
    builder.endpoint("price?stock=" + stock + "&days=" + days);
    builder.host(InetAddress.getLocalHost().getHostName());
    builder.processTime(totalTime + "(ms) ");
    builder.responseTime(responseTime + "(ms)");
    builder.timeStamp(start);
    stockMetadata = builder.build();

    // Create complete response object and pretty print JSON
    FullResponse fullResponse = new FullResponse(stockMetadata, filtered);
    return fullResponse;
  }
}
