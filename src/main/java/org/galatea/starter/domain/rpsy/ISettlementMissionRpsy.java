package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.SettlementMission;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ISettlementMissionRpsy extends CrudRepository<SettlementMission, Long> {

  List<SettlementMission> findByDepot(String depot);

  @Override
  @Cacheable(cacheNames = "missions", sync = true)
  SettlementMission findOne(Long id);
}
