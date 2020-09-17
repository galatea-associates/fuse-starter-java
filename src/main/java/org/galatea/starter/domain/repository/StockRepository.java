package org.galatea.starter.domain.repository;

import java.util.Collection;
import org.galatea.starter.domain.MongoDocument;

public interface StockRepository {
  boolean insert(MongoDocument mongoDocument, String ticker);
  boolean insertMany(Collection<MongoDocument> mongoDocuments, String ticker);
  void delete(MongoDocument mongoDocument);
  Collection<MongoDocument> findXDaysOfTicker(int xdays, String ticker);
}
