package org.galatea.starter;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessage;
import org.galatea.starter.utils.ObjectSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides beans used in unit tests.
 */
@Configuration
public class TestConfig {

  /**
   * @return a bean to create dummy trade agreements
   */
  @Bean
  public ObjectSupplier<TradeAgreement> tradeAgreementSupplier() {
    return () -> TradeAgreement.builder().id(0L).instrument("IBM").internalParty("INT-1")
        .externalParty("EXT-1").buySell("B").qty(100d).build();
  }

  /**
   * @return a bean to create dummy trade agreement messages
   */
  @Bean
  public ObjectSupplier<TradeAgreementMessage> tradeAgreementMessageSupplier() {
    return () -> TradeAgreementMessage.builder().instrument("IBM").internalParty("INT-1")
        .externalParty("EXT-1").buySell("B").qty(100d).build();
  }

  @Bean
  public ObjectSupplier<TradeAgreementProtoMessage> tradeAgreementProtoMessageSupplier() {
    return () -> TradeAgreementProtoMessage.newBuilder().setInstrument("IBM")
        .setInternalParty("INT-1").setExternalParty("EXT-1").setBuySell("B").setQty(100).build();
  }

  @Bean
  public ObjectSupplier<SettlementMission> settlementMissionSupplier() {
    return () -> SettlementMission.builder().id(100L).depot("DTC").externalParty("EXT-1")
        .instrument("IBM").direction("REC").qty(100d).build();
  }

}
