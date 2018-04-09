package org.galatea.starter;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.Messages.SettlementMissionMessage;
import org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessage;
import org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessages;
import org.galatea.starter.entrypoint.translation.SettlementMissionTranslator;
import org.galatea.starter.entrypoint.translation.TradeAgreementMessageTranslator;
import org.galatea.starter.entrypoint.translation.TradeAgreementMessagesTranslator;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MessageTranslationConfig {

  @Bean
  public ITranslator<SettlementMission, SettlementMissionMessage> settlementMissionTranslator() {
    return new SettlementMissionTranslator();
  }

  @Bean
  public ITranslator<TradeAgreementMessage, TradeAgreement> tradeAgreementMessageTranslator() {
    return new TradeAgreementMessageTranslator();
  }

  @Bean
  public ITranslator<TradeAgreementMessages, List<TradeAgreement>> tradeAgreementMessagesTranslator() {
    return new TradeAgreementMessagesTranslator(tradeAgreementMessageTranslator());
  }

}
