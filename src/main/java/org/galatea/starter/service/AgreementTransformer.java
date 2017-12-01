package org.galatea.starter.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.joda.money.BigMoney;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class AgreementTransformer implements IAgreementTransformer {

    @NonNull
    IProceedsCalculator proceedsCalc;

    @Override
    public SettlementMission transform(TradeAgreement agreement) {

        BigMoney base = BigMoney.parse("GBP 100");
        BigMoney usdProceeds = proceedsCalc.getUSDProceeds(base);
        log.info(usdProceeds.toString());

        return SettlementMission.builder().instrument(agreement.getInstrument())
            .externalParty(agreement.getExternalParty()).depot("DTC").qty(agreement.getQty())
            .direction("B".equals(agreement.getBuySell()) ? "REC" : "DEL").build();
    }
}
