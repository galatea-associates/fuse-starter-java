package org.galatea.starter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.service.PriceService.COIN;
import org.junit.Test;

public class HalServiceTest extends ASpringTest {

  @Test
  public void testProcessTestCoinFlipHeads() {
    String stock = "MSFT";
    String daysToLookBack = "10";
    String expResult = "Heads";

    PriceService service = new PriceService();
    PriceService spyService = spy(service);

    doReturn(COIN.HEADS).when(spyService).coinFlipRand();

    String result = spyService.processStock(stock, daysToLookBack);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestCoinFlipTails() {
    String stock = "MSFT";
    String daysToLookBack = "10";
    String expResult = "Tails";

    PriceService service = new PriceService();
    PriceService spyService = spy(service);

    doReturn(COIN.TAILS).when(spyService).coinFlipRand();

    String result = spyService.processStock(stock, daysToLookBack);
    assertEquals(expResult, result);
  }

  @Test
  public void testCoinFlipRand() {
    PriceService service = new PriceService();

    COIN result = service.coinFlipRand();
    assertThat(result).isIn(COIN.HEADS, COIN.TAILS);
  }

  @Test
  public void testProcessTestGetDerp() {
    String stock = "MSFT";
    String daysToLookBack = "10";
    String expResult = "derp!";

    PriceService service = new PriceService();

    String result = service.processStock(stock, daysToLookBack);
    assertEquals(expResult, result);
  }

  @Test
  public void testUnsupportedInput() {
    String stock = "MSFT";
    String daysToLookBack = "10";
    String expResult = "Unsupported command";

    PriceService service = new PriceService();

    String result = service.processStock(stock, daysToLookBack);
    assertEquals(expResult, result);
  }
}
