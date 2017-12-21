package org.galatea.starter.domain.rpsy;

import java.util.List;
import org.galatea.starter.domain.SettlementMission;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

public interface ISettlementMissionRpsy extends CrudRepository<SettlementMission, Long> {

  List<SettlementMission> findByDepot(String depot);

  @Override
  @Cacheable(cacheNames = "missions", sync = true)
  SettlementMission findOne(Long id);

}
