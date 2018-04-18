package org.galatea.starter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.service.HalService.COIN;
import org.junit.Test;

public class HalServiceTest extends ASpringTest {

  @Test
  public void testProcessTestCoinFlipHeads() {
    String text = "coin-flip";
    String expResult = "Heads";

    HalService service = new HalService();
    HalService spyService = spy(service);

    doReturn(COIN.HEADS).when(spyService).coinFlipRand();

    String result = spyService.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestCoinFlipTails() {
    String text = "coin-flip";
    String expResult = "Tails";

    HalService service = new HalService();
    HalService spyService = spy(service);

    doReturn(COIN.TAILS).when(spyService).coinFlipRand();

    String result = spyService.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testCoinFlipRand() {
    HalService service = new HalService();

    COIN result = service.coinFlipRand();
    assertThat(result).isIn(COIN.HEADS, COIN.TAILS);
  }

  @Test
  public void testProcessTestGetDerp() {
    String text = "derp";
    String expResult = "derp!";

    HalService service = new HalService();

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testUnsupportedInput() {
    String text = "Unsupported";
    String expResult = "Unsupported command";

    HalService service = new HalService();

    String result = service.processText(text);
    assertEquals(expResult, result);
  }
}
