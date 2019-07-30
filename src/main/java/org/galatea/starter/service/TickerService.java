package org.galatea.starter.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.SortedMap;
import org.galatea.starter.domain.Day;
import org.galatea.starter.domain.Ticker;
import org.galatea.starter.domain.rpsy.TickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Communicates with the TickerRepository and returns
 * correct ticker with desired number of days
 */
@Component
public class TickerService {

  @Autowired
  TickerRepository repository;

  /**
   * @param symbol
   * @param days
   * @return Ticker with correct symbol and number of days
   */
  public Ticker getTicker(String symbol, int days){
    Ticker ticker = findBySymbol(symbol);
    if(ticker == null){
      ticker = AlphaVantageService.getTicker(symbol);
      repository.save(ticker);
    }
    trimTicker(ticker,days);
    return ticker;
  }

  /**
   * @param symbol
   * @return Ticker with given symbol, NULL if not found
   */
  private Ticker findBySymbol(String symbol){
    for(Ticker ticker:repository.findAll()){
      if(ticker.getMetaData().getSymbol().equals(symbol)){
        return ticker;
      }
    }
    return null;
  }

  /**
   * Edits ticker to contain correct number of days
   * @param ticker
   * @param days
   */
  private void trimTicker(Ticker ticker, int days) {
    SortedMap<String, Day> timeSeries = new TreeMap<String, Day>(Collections.reverseOrder());
    List<String> set = new ArrayList<String>(ticker.getTimeSeries().keySet());
    Collections.reverse(set);
    for(int i = 0; i<days; i++){
      String day = set.get(i);
      timeSeries.put(day,ticker.getTimeSeries().get(day));
    }
    ticker.setTimeSeries(timeSeries);
  }
}