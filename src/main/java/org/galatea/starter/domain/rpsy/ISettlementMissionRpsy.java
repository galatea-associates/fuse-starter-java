package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.SettlementMission;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ISettlementMissionRpsy extends CrudRepository<SettlementMission, Long> {

  List<SettlementMission> findByDepot(String depot);

}
