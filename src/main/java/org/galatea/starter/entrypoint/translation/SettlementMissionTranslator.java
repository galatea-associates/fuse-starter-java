package org.galatea.starter.entrypoint.translation;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.entrypoint.messagecontracts.Messages;
import org.galatea.starter.utils.translation.ITranslator;

public class SettlementMissionTranslator implements
    ITranslator<SettlementMission, Messages.SettlementMissionMessage> {

  @Override
  public Messages.SettlementMissionMessage translate(SettlementMission mission) {
    return Messages.SettlementMissionMessage.newBuilder()
        .setId(mission.getId())
        .setInstrument(mission.getInstrument())
        .setExternalParty(mission.getExternalParty())
        .setDirection(mission.getDirection())
        .setDepot(mission.getDepot())
        .setQty(mission.getQty()).build();
  }
}
