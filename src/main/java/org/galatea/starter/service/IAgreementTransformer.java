package org.galatea.starter.service;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;

@FunctionalInterface
public interface IAgreementTransformer {

  /**
   * Creates a SettlementMission from the data in the given TradeAgreement.
   */
  SettlementMission transform(final TradeAgreement agreement);
}
