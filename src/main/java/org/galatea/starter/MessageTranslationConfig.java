package org.galatea.starter;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.Messages;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageTranslationConfig {

  @Bean
  public ITranslator<SettlementMission, Messages.SettlementMissionMessage> translateSettlementMission() {
    return mission -> Messages.SettlementMissionMessage.newBuilder()
        .setId(mission.getId())
        .setInstrument(mission.getInstrument())
        .setExternalParty(mission.getExternalParty())
        .setDirection(mission.getDirection())
        .setDepot(mission.getDepot())
        .setQty(mission.getQty()).build();
  }

  @Bean
  public ITranslator<Messages.TradeAgreementMessage, TradeAgreement> translateTradeAgreementMessage() {
    return message -> TradeAgreement.builder()
        .id(message.getId())
        .buySell(message.getBuySell())
        .externalParty(message.getExternalParty())
        .instrument(message.getInstrument())
        .internalParty(message.getInternalParty())
        .qty(message.getQty()).build();
  }

}
