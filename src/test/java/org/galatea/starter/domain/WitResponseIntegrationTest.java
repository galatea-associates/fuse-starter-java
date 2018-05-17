package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import junitparams.JUnitParamsRunner;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.wit.WitResponse;
import org.galatea.starter.restclient.WitGetter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(JUnitParamsRunner.class)
@ContextConfiguration
public class WitResponseIntegrationTest extends ASpringTest{

  //Test WitGetter Feign object for making HTTP requests. This mimics the defaultWitGetter defined in RestClientConfig.java
  @Autowired
  private ApplicationContext applicationContext;

  private WitGetter testGetter = applicationContext.getBean(WitGetter.class);
      //= Feign.builder().decoder(new JacksonDecoder()).target(WitGetter.class, "https://api.wit.ai");

  @Test
  public void testIntentRecognition(){

    String test = "flip a coin";
    WitResponse witRe = testGetter.getWitResponse("Bearer KGPXCMYTIUAJAWE7R4IVBBL7OTE7L7UE",test);
    //Check that the correct intent is extracted from the above String by wit.ai.
    assertEquals("coin-flip",witRe.getEntities().getIntent()[0].getValue());

    test = "Give me a movie quote";
    witRe = testGetter.getWitResponse("Bearer KGPXCMYTIUAJAWE7R4IVBBL7OTE7L7UE",test);
    assertEquals("movie-quote", witRe.getEntities().getIntent()[0].getValue());
  }

  @Test
  //Ensure the fields we expect to be populated are not null when we get a WitResponse back from a wit.ai request
  public void testFieldsNotNull(){

    String test = "flip a coin";
    WitResponse witRe = testGetter.getWitResponse("Bearer KGPXCMYTIUAJAWE7R4IVBBL7OTE7L7UE",test);
    assertNotNull(witRe.getEntities().getIntent());

    test = "Give me a movie quote";
    witRe = testGetter.getWitResponse("Bearer KGPXCMYTIUAJAWE7R4IVBBL7OTE7L7UE",test);
    assertNotNull(witRe.getEntities().getIntent());
  }

}
