package org.galatea.starter.service;

import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.MongoDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Front-facing service to request a number of days of share prices for the given stock.
 */
@RequiredArgsConstructor
@Slf4j
@Log
@Service
public class PriceRequestService {
  @Autowired
  private AlphaVantageService alphaVantageService;

  protected TreeMap<String, MongoDocument> externalRequest(String ticker, int days) {
    return alphaVantageService.access(ticker, days);
  }

  protected TreeMap<String, MongoDocument> internalRequest(String ticker, int days) {
    return null;
  }

  /**
   * Returns a TreeMap of MongoDocument values for each day of share price information
   * mapped to String keys of the corresponding day.
   * Might be less than requested if the information is unavailable,
   * so be sure to check length of the result.
   * @param ticker String, symbol of the stock being queried (case-insensitive)
   * @param days int, number of days to be returned if possible
   * @return TreeMap, date of info as a String key; prices info for that day as MongoDocument values
   */
  public TreeMap<String, MongoDocument> access(String ticker, int days) {
    boolean isInternal = false;

    //check if can get map from internal repo

    if (isInternal) {
      //get from internal repo and return
      return internalRequest(ticker, days);
    } else {
      return externalRequest(ticker, days);
    }
  }
}
