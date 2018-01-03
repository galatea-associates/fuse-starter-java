package org.galatea.starter.service;

import static org.junit.Assert.*;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;

public class AgreementTransformerTest extends ASpringTest {

	@MockBean
	private IProceedsCalculator mockProceedsCalc;

	@Test
	public void testTransform() {
		BigMoney proceeds = BigMoney.parse("GBP 100");

		TradeAgreement agreement = TradeAgreement.builder().instrument("IBM").internalParty("INT-1")
				.externalParty("EXT-1").buySell("B").qty(100d).proceeds(proceeds).build();

		BigMoney usdProceeds = BigMoney.of(CurrencyUnit.of("USD"), new BigDecimal(100));

		given(mockProceedsCalc.getUsdProceeds(proceeds)).willReturn(usdProceeds);

		AgreementTransformer agreementTransformer = new AgreementTransformer(mockProceedsCalc);

		SettlementMission settlementMission = agreementTransformer.transform(agreement);

		SettlementMission testSettlementMission = SettlementMission.builder().depot("DTC").externalParty("EXT-1")
				.instrument("IBM").direction("REC").qty(100d).proceeds(proceeds).usdProceeds(usdProceeds).build();

		assertTrue(settlementMission.equals(testSettlementMission));
	}
}
