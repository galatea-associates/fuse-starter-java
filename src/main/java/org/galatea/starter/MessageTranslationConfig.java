package org.galatea.starter;

import com.google.protobuf.InvalidProtocolBufferException;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.Messages.SettlementMissionMessage;
import org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessage;
import org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessages;
import org.galatea.starter.utils.translation.ITranslator;
import org.galatea.starter.utils.translation.TranslationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class MessageTranslationConfig {

  /**
   * @return a translator to convert binary protobuf messages to TradeAgreements
   */
  @Bean
  public ITranslator<byte[], TradeAgreement> tradeAgreementProtobufTranslator(
      ITranslator<TradeAgreementMessage, TradeAgreement> tradeAgreementMessageTranslator) {
    return msg -> {
      TradeAgreementMessage message;

      try {
        message = TradeAgreementMessage.parseFrom(msg);
      } catch (InvalidProtocolBufferException e) {
        throw new TranslationException("Could not translate the message to a trade agreement.", e);
      }

      return tradeAgreementMessageTranslator.translate(message);
    };
  }

  /**
   * @return a translator to convert SettlementMissions to protobuf messages.
   */
  @Bean
  public ITranslator<SettlementMission, SettlementMissionMessage> settlementMissionTranslator() {
    return mission -> SettlementMissionMessage.newBuilder()
        .setId(mission.getId())
        .setInstrument(mission.getInstrument())
        .setExternalParty(mission.getExternalParty())
        .setDirection(mission.getDirection())
        .setDepot(mission.getDepot())
        .setQty(mission.getQty()).build();
  }

  /**
   * @return a translator to convert protobuf messages to TradeAgreements
   */
  @Bean
  public ITranslator<TradeAgreementMessage, TradeAgreement> tradeAgreementMessageTranslator() {
    return message -> TradeAgreement.builder()
        .id(message.getId())
        .buySell(message.getBuySell())
        .externalParty(message.getExternalParty())
        .instrument(message.getInstrument())
        .internalParty(message.getInternalParty())
        .qty(message.getQty()).build();
  }

  /**
   * @return a translator to convert protobuf messages to a list of TradeAgreements
   */
  @Bean
  public ITranslator<TradeAgreementMessages, List<TradeAgreement>> tradeAgreementMessagesTranslator(
      ITranslator<TradeAgreementMessage, TradeAgreement> translator) {
    return messages -> messages.getMessageList().stream()
        .map(translator::translate)
        .collect(Collectors.toList());
  }

}
