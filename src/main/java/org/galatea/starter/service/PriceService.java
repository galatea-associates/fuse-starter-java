package org.galatea.starter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.ModelResponse.Prices;
import org.galatea.starter.domain.ModelResponse.TimeSeriesDaily;
import org.galatea.starter.service.feign.PricesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Log
@Service
@Component
public class PriceService implements PricesClient {

  /**
   * Process the stock from GET command into the appropriate command
   *
   * @param stock from the full text from the GET command. Wit.ai will break this down
   * @param daysToLookBack from the full text from the GET command. Wit.ai will break this down
   * @return the result of executing the command with the given parameters
   */

  @Autowired
  private PricesClient pricesclient;
  private Object TimeSeriesDaily;


  //get JSON response from AlphaVantage
  @Override
  public TimeSeriesDaily getPricesByStock(String stock) {
    TimeSeriesDaily obj_prices = pricesclient.getPricesByStock(stock);
    String str_prices = obj_prices.toString();

    //Parse dates from the TimeSeriesDaily


    // Map JSON to individual objects (key: Date, value: price)
    ObjectMapper mapper = new ObjectMapper();
    try{
      Map<Object, Object> map = mapper.readValues(obj_prices, Prices.class);
    } catch (IOException e){
      e.printStackTrace();
    }




    return obj_prices;

  }




    //method to return parameters of User's request
    public String processStock (String stock, Integer daysToLookBack){
      String parameters = stock + " " + daysToLookBack;
      return parameters;
     }
}

//place holder for switch based on # of days requested


//// Return previous method as string
//  public String getPricesByStock(String stock) {
//    String obj_prices = pricesclient.getPricesByStock(stock);
//    String str_prices = obj_prices.toString();
//
//// Custom Deserializer
// Class DateHandler extends StdDeserializer<Void> {
//
//      public DateHandler() {
//        this(null);
//
//      }
//
//      public DateHandler(Class<?> clazz) {
//        super(clazz);
//      }
//
//      @Override
//      public Void deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
//          throws IOException, JsonProcessingException {
//        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
//        Date id = (LocalDate) ((IntNode) node.get("id")).numberValue();
////        String date = JsonParser.getText();
////        try {
////          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
////          return sdf.parse(date);
////        } catch (Exception e) {
////          return null;
////          }
////        }