package org.galatea.starter.service;

import java.util.List;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.MongoDocument;
import org.galatea.starter.domain.rpsy.StockPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

  @Autowired
  private StockPriceRepository stockPriceRepository;

  protected TreeMap<String, MongoDocument> externalRequest(final String ticker, final int days) {
    return alphaVantageService.access(ticker, days);
  }

  protected TreeMap<String, MongoDocument> internalRequest(final String ticker, final int days) {
    Slice<MongoDocument> documentSlice = stockPriceRepository
        .findByTicker(ticker,
            PageRequest.of(0, days, Sort.by(Direction.DESC, "date")));
    //checks if the info we're looking for exists in the repo
    if (documentSlice.hasContent()) {
      List<MongoDocument> documents = documentSlice.getContent();
      TreeMap<String, MongoDocument> treeMap = new TreeMap<>();

      for (MongoDocument document : documents) {
        treeMap.put(document.getDate().toString(), document);
      }
      return treeMap;
    } else {
      return null; //default response that tells caller to poke AlphaVantage for info
    }
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
  public TreeMap<String, MongoDocument> access(final String ticker, final int days) {
    //check if can get map from internal repo
    TreeMap<String, MongoDocument> result = internalRequest(ticker, days);
    if (result == null) {
      return externalRequest(ticker, days); //query AlphaVantage and serve
    } else {
      return result;
    }
  }
}
