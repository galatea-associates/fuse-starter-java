package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.MongoDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockPriceRepository extends MongoRepository<MongoDocument, String> {

  /**
   * Custom query to find ...stub
   *
   * @param ticker String, stock ticker symbol
   * @param pageable stub
   * @return Slice of the `days` number of stock price documents
   */
  Slice<MongoDocument> findByTicker(String ticker, Pageable pageable);
}
