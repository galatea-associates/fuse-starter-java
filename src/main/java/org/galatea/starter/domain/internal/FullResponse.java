package org.galatea.starter.domain.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.internal.StockMetadata.StockMetadataBuilder;
import org.galatea.starter.service.PriceService;


@Data
@AllArgsConstructor
@Log
@Slf4j
public class FullResponse {





  public ArrayList<StockMetadata> BuildMeta (Long startTime, Long process, String stock, String days)
      throws UnknownHostException {


    PriceService priceService = new PriceService();
    StockMetadata stockMetadata;
    ArrayList<StockMetadata> metadata = new ArrayList<>();


//    String a = Long.toString(process);
//    String b = Long.toString(startTime);
//    System.out.println(a + b);

    StockMetadataBuilder builder = StockMetadata.builder();
    builder.endpoint("price?stock=" + stock + "&days=" + days);
    builder.host(InetAddress.getLocalHost().getHostName());
    builder.responseTime(process.intValue());
    builder.timeStamp(startTime.intValue());
    stockMetadata = builder.build();

    metadata.add(stockMetadata);
    log.info ("Meta Data: {}", metadata);
    System.out.println(metadata);

//    JSONObject fullResponse = new JSONObject();
//    fullResponse.put("MetaData", stockMetadata);

//    JSONObject fullResponse = new JSONObject();
//    fullResponse.put("Stock Prices", priceService);

    return metadata;
  }
}
