package org.galatea.starter;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.Messages;
import org.galatea.starter.entrypoint.translation.SettlementMissionTranslator;
import org.galatea.starter.entrypoint.translation.TradeAgreementMessageTranslator;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageTranslationConfig {

  @Bean
  public ITranslator<SettlementMission, Messages.SettlementMissionMessage> settlementMissionTranslator() {
    return new SettlementMissionTranslator();
  }

  @Bean
  public ITranslator<Messages.TradeAgreementMessage, TradeAgreement> tradeAgreementMessageTranslator() {
    return new TradeAgreementMessageTranslator();
  }

}
