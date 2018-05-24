package org.galatea.starter.service;

import static org.mockito.BDDMockito.given;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.Map;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.RestClientConfig;
import org.galatea.starter.domain.Quote;
import org.galatea.starter.domain.wit.Entity;
import org.galatea.starter.domain.wit.EntityStore;
import org.galatea.starter.domain.wit.WitResponse;
import org.galatea.starter.restclient.QuoteGetter;
import org.galatea.starter.restclient.WitGetter;
import org.galatea.starter.service.HalService.COIN;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;


@ContextConfiguration(classes = {HalService.class},initializers = ConfigFileApplicationContextInitializer.class)
@TestPropertySource(locations = {"classpath:application.yml"})
public class HalServiceTest extends ASpringTest {

  @MockBean
  private QuoteGetter mockQuoteGetter;

  @MockBean
  private WitGetter mockWitGetter;

  @Value("${wit.token}")
  private String witToken;

  private WitResponse CreateTestResponse(String intent){
    Entity ent = new Entity(1, intent);
    Entity[] eArr = new Entity[1];
    eArr[0] = ent;
    EntityStore eStore = new EntityStore();
    eStore.setIntent(eArr);
    WitResponse witRe = new WitResponse();
    witRe.setEntities(eStore);
    witRe.setText("This is a sample WitResponse");
    return witRe;
  }



  @Test
  public void testProcessTestCoinFlipHeads() {
    String text = "coin-flip";
    String expResult = "Heads";
    //Create a mock witResponse object and assign "coin-flip" as the intent with confidence of 1
    WitResponse witRe = CreateTestResponse(text);

    given(this.mockWitGetter.getWitResponse("Bearer " + witToken,text)).willReturn(witRe);

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);
    ReflectionTestUtils.setField(service, "witToken", witToken);

    HalService spyService = spy(service);
    doReturn(COIN.HEADS).when(spyService).coinFlipRand();

    String result = spyService.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestCoinFlipTails() {
    String text = "coin-flip";
    String expResult = "Tails";
    //Create a mock witResponse object and assign "coin-flip" as the intent with confidence of 1
    WitResponse witRe = CreateTestResponse(text);

    given(this.mockWitGetter.getWitResponse("Bearer " + witToken,text)).willReturn(witRe);

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);
    ReflectionTestUtils.setField(service, "witToken", witToken);

    HalService spyService = spy(service);
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

    //Create a mock witResponse object and assign "num-galateans" as the intent with confidence of 1
    WitResponse witRe = CreateTestResponse(text);
    given(this.mockWitGetter.getWitResponse("Bearer " + witToken,text)).willReturn(witRe);

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);
    ReflectionTestUtils.setField(service, "witToken", witToken);

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

  @Test
  public void testProcessTestGetRecReading() {
    String text = "rec-reading";
    String expResult = "https://docs.google.com/spreadsheets/d/1rxtbvuoMvKRdAbgIUKuis-8c5Pdyptvg03m23hikOIM/";

    //Create a mock witResponse object and assign "rec-reading" as the intent with confidence of 1
    WitResponse witRe = CreateTestResponse(text);
    given(this.mockWitGetter.getWitResponse("Bearer " + witToken,text)).willReturn(witRe);

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);
    ReflectionTestUtils.setField(service, "witToken", witToken);

    String result = service.processText(text);
    assertEquals(expResult, result);
  }


  @Test
  public void testProcessTestGetMovieQuote() {
    String text = "movie-quote";
    String quoteText = "This mission is too important for me to allow you to jeopardize it";
    String quoteFrom = "2001 A Space Odyssey";
    Quote[] quote = new Quote[1];
    quote[0] = new Quote();
    quote[0].setQuoteText(quoteText);
    quote[0].setAuthor(quoteFrom);

    //Create a mock witResponse object and assign "movie-quote" as the intent with confidence of 1
    WitResponse witRe = CreateTestResponse(text);
    given(this.mockWitGetter.getWitResponse("Bearer " + witToken,text)).willReturn(witRe);

    QuoteGetter quoteGetter = new QuoteGetter() {
      @Override
      public Quote[] getQuote() {
        return quote;
      }
    };

    HalService service = new HalService(quoteGetter, mockWitGetter);
    ReflectionTestUtils.setField(service, "witToken", witToken);

    String result = service.processText(text);
    assertEquals(("Quote: "+quoteText+", from: 2001 A Space Odyssey"), result);
  }


  @Test
  public void testProcessTestGetDerp() {
    String text = "derp";
    String expResult = "derp!";

    //Create a mock witResponse object and assign "derp" as the intent with confidence of 1
    WitResponse witRe = CreateTestResponse(text);
    given(this.mockWitGetter.getWitResponse("Bearer " + witToken,text)).willReturn(witRe);

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);
    ReflectionTestUtils.setField(service, "witToken", witToken);

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

  //This test will change when we define a way to handle requests with no identified intent.

  @Test
  public void testUnsupportedInput() {
    String text = "Unsupported";
    String expResult = "Unsupported command";

    //Create a mock witResponse object and assign "unsupported" as the intent with confidence of 1
    WitResponse witRe = CreateTestResponse(text);
    given(this.mockWitGetter.getWitResponse("Bearer " + witToken,text)).willReturn(witRe);

    HalService service = new HalService(mockQuoteGetter, mockWitGetter);
    ReflectionTestUtils.setField(service, "witToken", witToken);

    String result = service.processText(text);
    assertEquals(expResult, result);
  }

}
