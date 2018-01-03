package org.galatea.starter.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AgreementTransformer implements IAgreementTransformer {

  @NonNull
  IProceedsCalculator proceedsCalc;

  @Override
  public SettlementMission transform(TradeAgreement agreement) {
    SettlementMission mission =
        SettlementMission.builder()
            .instrument(agreement.getInstrument())
            .externalParty(agreement.getExternalParty())
            .depot("DTC")
            .qty(agreement.getQty())
            .direction("B".equals(agreement.getBuySell()) ? "REC" : "DEL")
            .proceeds(agreement.getProceeds())
            .usdProceeds(proceedsCalc.getUSDProceeds(agreement.getProceeds()))
            .build();

    log.info(mission.toString());
    return mission;
  }
}
