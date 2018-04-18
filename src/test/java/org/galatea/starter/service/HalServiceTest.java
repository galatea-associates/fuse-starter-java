package org.galatea.starter.service;

import static org.mockito.BDDMockito.given;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.Map;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.Quote;
import org.galatea.starter.domain.wit.Entity;
import org.galatea.starter.domain.wit.EntityStore;
import org.galatea.starter.domain.wit.WitResponse;
import org.galatea.starter.restClient.QuoteGetter;
import org.galatea.starter.restClient.WitGetter;
import org.galatea.starter.service.HalService.COIN;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class HalServiceTest extends ASpringTest {

  @MockBean
  private QuoteGetter mockQuoteGetter;

  @MockBean
  private WitGetter mockWitGetter;

  @Test
  public void testProcessTestCoinFlipHeads() {
    String text = "coin-flip";
    String expResult = "Heads";

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);
    HalService spyService = spy(service);

    //Create a mock witResponse object and assign "coin-flip" as the intent with confidence of 1
    Entity ent = new Entity(1, text);
    Entity[] eArr = new Entity[1];
    eArr[0] = ent;
    EntityStore eStore = new EntityStore();
    eStore.setIntent(eArr);
    WitResponse witRe = new WitResponse(text, eStore);

    given(this.mockWitGetter.getWitResponse(text)).willReturn(witRe);
    doReturn(COIN.HEADS).when(spyService).coinFlipRand();

    String result = spyService.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestCoinFlipTails() {
    String text = "coin-flip";
    String expResult = "Tails";

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);
    HalService spyService = spy(service);

    //Create a mock witResponse object and assign "coin-flip" as the intent with confidence of 1
    Entity ent = new Entity(1, text);
    Entity[] eArr = new Entity[1];
    eArr[0] = ent;
    EntityStore eStore = new EntityStore();
    eStore.setIntent(eArr);
    WitResponse witRe = new WitResponse(text, eStore);

    given(this.mockWitGetter.getWitResponse(text)).willReturn(witRe);
    doReturn(COIN.TAILS).when(spyService).coinFlipRand();

    String result = spyService.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestGetNumGalateans() {
    String text = "num-galateans";
    Map<String, Integer> map = new HashMap<>();
    map.put("Florida", 6);
    map.put("London", 13);
    map.put("Boston", 50);
    map.put("NorthCarolina", 5);

    String expResult = map.toString();

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);

    //Create a mock witResponse object and assign "num-galateans" as the intent with confidence of 1
    Entity ent = new Entity(1, text);
    Entity[] eArr = new Entity[1];
    eArr[0] = ent;
    EntityStore eStore = new EntityStore();
    eStore.setIntent(eArr);
    WitResponse witRe = new WitResponse(text, eStore);

    given(this.mockWitGetter.getWitResponse(text)).willReturn(witRe);

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestGetRecReading() {
    String text = "rec-reading";
    String expResult = "https://docs.google.com/spreadsheets/d/1rxtbvuoMvKRdAbgIUKuis-8c5Pdyptvg03m23hikOIM/";

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);

    //Create a mock witResponse object and assign "rec-reading" as the intent with confidence of 1
    Entity ent = new Entity(1, text);
    Entity[] eArr = new Entity[1];
    eArr[0] = ent;
    EntityStore eStore = new EntityStore();
    eStore.setIntent(eArr);
    WitResponse witRe = new WitResponse(text, eStore);

    given(this.mockWitGetter.getWitResponse(text)).willReturn(witRe);

    String result = service.processText(text);
    assertEquals(expResult, result);
  }


  @Test
  public void testProcessTestGetMovieQuote() {
    String text = "movie-quote";
    String quoteText = "This mission is too important for me to allow you to jeopardize it";
    String quoteFrom = "2001 A Space Odyssey";
    Quote quote = new Quote();
    quote.setQuote(quoteText);
    quote.setAuthor(quoteFrom);

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);

    //Create a mock witResponse object and assign "movie-quote" as the intent with confidence of 1
    Entity ent = new Entity(1, text);
    Entity[] eArr = new Entity[1];
    eArr[0] = ent;
    EntityStore eStore = new EntityStore();
    eStore.setIntent(eArr);
    WitResponse witRe = new WitResponse(text, eStore);

    //Mock the behavior of methods that will be called by HalService when it encounters a "movie-quote" intent
    given(this.mockQuoteGetter.getQuote()).willReturn(quote);
    given(this.mockWitGetter.getWitResponse(text)).willReturn(witRe);

    String result = service.processText(text);
    assertEquals(("Quote: "+quoteText+", from: 2001 A Space Odyssey"), result);
  }


  @Test
  public void testProcessTestGetDerp() {
    String text = "derp";
    String expResult = "derp!";

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);

    //Create a mock witResponse object and assign "derp" as the intent with confidence of 1
    Entity ent = new Entity(1, text);
    Entity[] eArr = new Entity[1];
    eArr[0] = ent;
    EntityStore eStore = new EntityStore();
    eStore.setIntent(eArr);
    WitResponse witRe = new WitResponse(text, eStore);

    given(this.mockWitGetter.getWitResponse(text)).willReturn(witRe);

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

  //This test will change when we define a way to handle requests with no identified intent.

  @Test
  public void testUnsupportedInput() {
    String text = "Unsupported";
    String expResult = "Unsupported command";

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);

    //Create a mock witResponse object and assign "unsupported" as the intent with confidence of 1
    Entity ent = new Entity(1, text);
    Entity[] eArr = new Entity[1];
    eArr[0] = ent;
    EntityStore eStore = new EntityStore();
    eStore.setIntent(eArr);
    WitResponse witRe = new WitResponse(text, eStore);

    given(this.mockWitGetter.getWitResponse(text)).willReturn(witRe);

    String result = service.processText(text);
    assertEquals(expResult, result);
  }
}
