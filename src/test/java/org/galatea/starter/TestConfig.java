package org.galatea.starter;

import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessage;
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
    return () -> TradeAgreement.builder().id(0l).instrument("IBM").internalParty("INT-1")
        .externalParty("EXT-1").buySell("B").qty(100d).build();
  }

  /**
   * @return a bean to create dummy trade agreement messages
   */
  @Bean
  public ObjectSupplier<TradeAgreementMessage> tradeAgreementMessageSupplier() {
    return () -> TradeAgreementMessage.newBuilder().setInstrument("IBM").setInternalParty("INT-1")
        .setExternalParty("EXT-1").setBuySell("B").setQty(100).build();
  }
}
