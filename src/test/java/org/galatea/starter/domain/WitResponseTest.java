package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import feign.Feign;
import feign.gson.GsonDecoder;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.wit.WitResponse;
import org.galatea.starter.restclient.WitGetter;
import org.junit.Test;


public class WitResponseTest extends ASpringTest{

  //Test WitGetter Feign object for making HTTP requests. This mimics the defaultWitGetter defined in WitConfig.java
  WitGetter testGetter = Feign.builder().decoder(new GsonDecoder()).target(WitGetter.class, "https://api.wit.ai");

  @Test
  public void testIntentRecognition(){

    String test = "flip a coin";
    WitResponse witRe = testGetter.getWitResponse(test);
    //Check that the correct intent is extracted from the above String by wit.ai.
    assertEquals("coin-flip",witRe.getEntities().getIntent()[0].getValue());

    test = "Give me a movie quote";
    witRe = testGetter.getWitResponse(test);
    assertEquals("movie-quote", witRe.getEntities().getIntent()[0].getValue());
  }

  @Test
  //Ensure the fields we expect to be populated are not null when we get a WitResponse back from a wit.ai request
  public void testFieldsNotNull(){

    String test = "flip a coin";
    WitResponse witRe = testGetter.getWitResponse(test);
    assertNotNull(witRe.getEntities().getIntent());

    test = "Give me a movie quote";
    witRe = testGetter.getWitResponse(test);
    assertNotNull(witRe.getEntities().getIntent());
  }

}
