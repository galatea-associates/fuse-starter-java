package org.galatea.starter;

import com.google.protobuf.InvalidProtocolBufferException;
import java.util.List;
import java.util.stream.Collectors;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementMissionProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessages;
import org.galatea.starter.utils.translation.ITranslator;
import org.galatea.starter.utils.translation.TranslationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProtoMessageTranslationConfig {

  /**
   * Implements a translator to convert binary protobuf messages to TradeAgreements
   *
   * <p>This translator is used for the protobuf JMS listener. The SimpleMessageConverter we use in
   * that listener gives back a byte[] containing a serialized TradeAgreementProtoMessage, so we
   * need to perform two steps to get to the TradeAgreement we want: convert the byte array back to
   * a TradeAgreementProtoMessage and then translate it to the internal domain type.
   */
  @Bean
  public ITranslator<byte[], TradeAgreement> tradeAgreementBinaryProtobufTranslator(
      final ITranslator<TradeAgreementProtoMessage, TradeAgreement>
          tradeAgreementProtoMessageTranslator) {
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

  /**
   * Implements a translator to convert TradeAgreement protobuf messages to TradeAgreement domain
   * objects.
   */
  @Bean
  public ITranslator<TradeAgreementProtoMessage, TradeAgreement> tradeAgreementProtoTranslator() {
    return msg -> TradeAgreement.builder().buySell(msg.getBuySell())
        .externalParty(msg.getExternalParty()).internalParty(msg.getInternalParty())
        .instrument(msg.getInstrument()).qty(msg.getQty()).build();
  }

  /**
   * Implements a translator to convert domain SettlementMission objects to SettlementMission
   * protobuf messages.
   */
  @Bean
  public ITranslator<SettlementMission, SettlementMissionProtoMessage>
      settlementMissionProtoTranslator() {
    return msg -> SettlementMissionProtoMessage.newBuilder().setId(msg.getId())
        .setDepot(msg.getDepot()).setDirection(msg.getDirection())
        .setExternalParty(msg.getExternalParty()).setInstrument(msg.getInstrument())
        .setQty(msg.getQty()).setVersion(msg.getVersion()).build();
  }

  /**
   * Implements a translator to convert a TradeAgreement protobuf collection to a list of
   * TradeAgreement domain objects.
   */
  @Bean
  public ITranslator<TradeAgreementProtoMessages, List<TradeAgreement>>
      tradeAgreementProtoMessagesTranslator(
      final ITranslator<TradeAgreementProtoMessage, TradeAgreement> translator) {
    return msg -> msg.getMessageList().stream().map(translator::translate)
        .collect(Collectors.toList());
  }
}
