package org.galatea.starter.domain.repository;

import java.util.Collection;
import org.galatea.starter.domain.MongoDocument;

public interface StockRepository {

  /**
   * Inserts a single document into the repository.
   *
   * @param mongoDocument document to be inserted
   * @param ticker String, stock symbol corresponding to collection/table inserted into
   * @return True if inserted successfully, else false
   */
  boolean insert(MongoDocument mongoDocument, String ticker);

  /**
   * Inserts the given Collection of documents into the repository.
   *
   * @param mongoDocuments documents to be inserted
   * @param ticker String, stock symbol corresponding to collection/table inserted into
   * @return True if inserted successfully, else false
   */
  boolean insertMany(Collection<MongoDocument> mongoDocuments, String ticker);

  /**
   * Deletes a MongoDocument from repository.
   *
   * @param mongoDocument MongoDocument, object to remove from repository
   */
  void delete(MongoDocument mongoDocument);

  /**
   * Returns a Collection of MongoDocuments filtered by the number of days of documetns to retrieve.
   *
   * @param xdays int, number of days in past to retrieve documents
   * @param ticker String, the stock ticker symbol of company to retrieve
   * @return Collection, MongoDocuments up to `xdays` ago.
   */
  Collection<MongoDocument> findXDaysOfTicker(int xdays, String ticker);
}
