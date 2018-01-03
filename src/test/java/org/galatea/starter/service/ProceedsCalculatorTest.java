package org.galatea.starter.service;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.FXRateResponse;
import org.galatea.starter.service.client.IFXRestClient;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

public class ProceedsCalculatorTest extends ASpringTest {

	@MockBean
	private IFXRestClient restClient;

	private IProceedsCalculator proceedsCalc;

	private BigMoney proceeds;

	private BigMoney USDProceeds;

	@Before
	public void setUp() {
		proceedsCalc = new ProceedsCalculator(restClient);
		proceeds = BigMoney.of(CurrencyUnit.of("GBP"), BigDecimal.valueOf(100));
	}

	@Test
	public void testGetUSDProceedsSuccess() {

		given(restClient.getRate(proceeds.getCurrencyUnit().getCode()))
				.willReturn(FXRateResponse.builder().baseCurrency(CurrencyUnit.of("GBP"))
						.exchangeRate(BigDecimal.valueOf(1.33d)).validOn(LocalDate.now()).build());

		USDProceeds = proceedsCalc.getUSDProceeds(proceeds);

		// this works but doesn't feel correct 
		assertTrue(BigMoney.of(CurrencyUnit.of("USD"), 133d).compareTo(USDProceeds) == 0);

		// this does not work -> expected <USD 133> but was <USD 133.00>
		// assertEquals(BigMoney.of(CurrencyUnit.of("USD"), 133d), USDProceeds);

	}

	@Test
	public void testGetUSDProceedsFromUSD() {
		// case when the initial currency is USD already, so we expect the same result
		// back.
		proceeds = BigMoney.of(CurrencyUnit.of("USD"), BigDecimal.valueOf(100));
		USDProceeds = proceedsCalc.getUSDProceeds(proceeds);

		assertEquals(proceeds, USDProceeds);
	}

}
