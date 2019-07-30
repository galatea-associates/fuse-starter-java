package org.galatea.starter.TickerInfo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TickerInfoRepository extends MongoRepository<Ticker,String> {


}
