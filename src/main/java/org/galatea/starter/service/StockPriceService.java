package org.galatea.starter.service;

import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.AlphaVantageResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * A layer for transformation, aggregation, and business required when retrieving data from Alpha Vantage.
 */
@Slf4j
@Service
@RequiredArgsConstructor

public class StockPriceService {

  @NonNull
  private StockPriceClient StockPriceClient;
  private Integer days;

  /**
   * Get the prices for the Symbol that is passed in.
   *
   * @param //days the number of days to get prices for that ticker.
   * @param //symbol the symbol to get open, high, low and close prices for.
   * @return a list of prices objects for that symbol for only the specified.
   */
  public AlphaVantageResponse getPricesForSymbolForLastNDays(final String symbol, final Integer days) {
    return StockPriceClient.getPricesForSymbolForLastNDays(symbol, days);
  }

}

  //get dates to determine whether the call to AV should be compact or full
  /*public List<> getDatesNeededForStockData(final List<String> days){
    return StockPriceClient.getDatesNeededForStockData(days.toArray(new String[0]));
  }*/

// Define number of days N and how it wll be used to limit results by only those days */
//store today's date as a getDate() function (t)
//DateFormat df = new SimpleDateFormat("dd-MM-yyyy")
//store number of days in days variable (n)
//count every valid day for t-1, t-2, t-3,.. t-n
//need to use today's date and valid days to calculate start date
//count valid days by defining weekdays M-F 5 days, weekend Sat/Sun, 2 days (local date, java can define weekdays)
//MM-DD-YY - number of days subtracts from DD if days < DD, to get start date
//MM-DD-YY - number of days subtracts from DD and MM if days > DD, to get start date
//MM-DD-YY - number of days subtracts from DD, MM and YY if days > DD and # of days request > # of days this year
//Months Jan, Mar, May, July, August, October, December (31 days), April, June, Sept, Nov (30 days), Feb (29 days) (leap yr?)

//for loop - for each day prior to today and through t-n, count the number of valid days, where n is number of days the client enters
//for(int i=0; i<n; i--){
//count(/*valid days from today to t-n*/)
/*
    }
    //query database for the specified ticker
    //@Query
    // if ticker exists, query prices for the last N days
    // if all prices are available (for last N days) in the database, pull all data from database
    //use unique ID here to retrieve all data points that are available for given ticker? findbyTicker() or findByIds()?
    //if (//data is in StockPriceTable//){
    //return ticker, date, and prices results from database
    //return StockPriceTable.toArray();
  }
  //if only some data is available in the database for any of the last N days, then pull info from the Database first
  // and pull the remaining data from Alpha Vantage
  //query database for the specified ticker
  // if ticker exists, query prices for the last N days
  // if some prices are there, output results for existing days
      //////////  if (/*some data is in StockPrice Table){
    //return available data from database
    //use StockPriceService to pull the rest from AV
  }
  //If no data is available in the database for last n days, pull all data from Alpha Vantage API
  // //////////else if (no data is available in database) {
    //return StockPriceClient.getPricesForSymbolForLastNDays(symbol.toArray(new String[0]));
  }
  //}
}*/
