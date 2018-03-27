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
  public void testProcessTestCoinFlip() throws Exception {
    String text = "coin-flip";
    String expResult = "Heads";
    ;

    HalService service = new HalService();
    HalService spyService = spy(service);

    doReturn(COIN.HEADS).when(spyService).coinFlipRand();

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestGetNumGalateans() throws Exception {
    String text = "num-galateans";
    Map<String, Integer> map = new HashMap<String, Integer>();
    map.put("Florida", 6);
    map.put("London", 13);
    map.put("Boston", 50);
    map.put("NorthCarolina", 5);

    String expResult = map.toString();

    HalService service = new HalService();

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestGetRecReading() throws Exception {
    String text = "rec-reading";
    String expResult = "https://docs.google.com/spreadsheets/d/1rxtbvuoMvKRdAbgIUKuis-8c5Pdyptvg03m23hikOIM/";

    HalService service = new HalService();

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestGetMovieQuote() throws Exception {
    String text = "movie-quote";
    String expResult = "This mission is too important for me to allow you to jeopardize it";

    HalService service = new HalService();

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestGetDerp() throws Exception {
    String text = "derp";
    String expResult = "derp!";

    HalService service = new HalService();

    String result = service.processText(text);
    assertEquals(expResult, result);
  }
}
