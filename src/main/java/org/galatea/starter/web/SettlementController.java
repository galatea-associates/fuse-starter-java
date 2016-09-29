package org.galatea.starter.web;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.rpsy.ISettlementMissionRpsy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@ToString
@EqualsAndHashCode
public class SettlementController {

  @Autowired
  ISettlementMissionRpsy missionrpsy;

  @RequestMapping("/save")
  public SettlementMission saveMission() {
    SettlementMission m =
        SettlementMission.builder().externalParty("GS").depot("DTC").qty(100).build();
    missionrpsy.save(m);
    return m;
  }

  @RequestMapping("/query")
  public List<SettlementMission> queryMission() {
    return missionrpsy.findByDepot("DTC");
  }



}
