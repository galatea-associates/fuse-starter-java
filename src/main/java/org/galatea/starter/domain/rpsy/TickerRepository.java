package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.Ticker;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TickerRepository extends MongoRepository<Ticker,String> {

}
