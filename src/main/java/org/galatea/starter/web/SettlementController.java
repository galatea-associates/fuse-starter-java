package org.galatea.starter.web;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.rpsy.ISettlementMissionRpsy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@ToString
@EqualsAndHashCode
public class SettlementController {

  private static final Logger LOGGER = LoggerFactory.getLogger(SettlementController.class);

  @Autowired
  ISettlementMissionRpsy missionrpsy;

  @RequestMapping("/save")
  public SettlementMission saveMission() {
    LOGGER.info("Saving mission");
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
