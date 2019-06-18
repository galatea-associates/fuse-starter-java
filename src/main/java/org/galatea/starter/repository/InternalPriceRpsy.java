package org.galatea.starter.repository;

import org.galatea.starter.domain.internal.InternalPrices;
import org.galatea.starter.domain.internal.StockPrices;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternalPriceRpsy extends CrudRepository <StockPrices, Long> {

  static void save(InternalPrices convertedPrices) {
  }
}
