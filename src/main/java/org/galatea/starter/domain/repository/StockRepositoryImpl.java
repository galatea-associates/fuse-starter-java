package org.galatea.starter.domain.repository;

import java.time.Instant;
import java.util.Collection;
import org.galatea.starter.domain.MongoDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class StockRepositoryImpl implements StockRepository {
  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public boolean insert(MongoDocument mongoDocument, String ticker) {
    mongoTemplate.insert(mongoDocument, ticker);
    return true;
  }

  @Override
  public boolean insertMany(Collection<MongoDocument> mongoDocuments, String ticker) {
    mongoTemplate.insert(mongoDocuments, ticker);
    return true;
  }

  @Override
  public void delete(MongoDocument mongoDocument) {
    mongoTemplate.remove(mongoDocument);
  }

  @Override
  public Collection<MongoDocument> findXDaysOfTicker(int xdays, String ticker) {
    Query findQuery = new Query(Criteria.where("date").lte(Instant.now().toString())); //not right
    Collection<MongoDocument> result = mongoTemplate.find(findQuery, MongoDocument.class, ticker);
    return result;
  }
}
