package org.galatea.starter.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.Map;
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
  public void testProcessTestGetNumGalateans() {
    String text = "num-galateans";
    String expResult = "";

    HalService service = new HalService();

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestGetRecReading() {
    String text = "rec-reading";
    String expResult = "";

    HalService service = new HalService();

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestGetMovieQuote() {
    String text = "movie-quote";
    String expResult = "";

    HalService service = new HalService();

    String result = service.processText(text);
    assertEquals(expResult, result);
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
