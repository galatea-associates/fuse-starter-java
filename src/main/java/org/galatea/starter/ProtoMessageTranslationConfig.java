package org.galatea.starter;

import com.google.protobuf.InvalidProtocolBufferException;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementMissionProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessage;
import org.galatea.starter.utils.translation.ITranslator;
import org.galatea.starter.utils.translation.TranslationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProtoMessageTranslationConfig {

  /**
   * @return a translator to convert binary protobuf messages to TradeAgreements
   */
  @Bean
  public ITranslator<byte[], TradeAgreement> tradeAgreementBinaryProtobufTranslator(
      ITranslator<TradeAgreementProtoMessage, TradeAgreement> tradeAgreementProtoMessageTranslator) {
    return msg -> {
      TradeAgreementProtoMessage message;

      try {
        message = TradeAgreementProtoMessage.parseFrom(msg);
      } catch (InvalidProtocolBufferException e) {
        throw new TranslationException("Could not translate the message to a trade agreement.", e);
      }

      return tradeAgreementProtoMessageTranslator.translate(message);
    };
  }

  @Bean
  public ITranslator<TradeAgreementProtoMessage, TradeAgreement> tradeAgreementProtoTranslator() {
    return msg -> TradeAgreement.builder().id(msg.getId()).buySell(msg.getBuySell())
        .externalParty(msg.getExternalParty()).internalParty(msg.getInternalParty())
        .instrument(msg.getInstrument()).qty(msg.getQty()).build();
  }

  @Bean
  public ITranslator<SettlementMission, SettlementMissionProtoMessage> settlementMissionProtoTranslator() {
    return msg -> SettlementMissionProtoMessage.newBuilder().setId(msg.getId())
        .setDepot(msg.getDepot()).setDirection(msg.getDirection())
        .setExternalParty(msg.getExternalParty()).setInstrument(msg.getInstrument())
        .setQty(msg.getQty()).build();
  }
}
