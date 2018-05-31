package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.RestClientConfig;
import org.galatea.starter.domain.wit.WitResponse;
import org.galatea.starter.restclient.WitGetter;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;

//This allows us to autowire the beans defined in RestClientConfig.java
@SpringBootTest(classes = {RestClientConfig.class})
@Import({FeignAutoConfiguration.class})
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class WitResponseIntegrationTest extends ASpringTest{

  @Autowired
  private WitGetter testGetter;

  @Value("${wit.token}")
  private String witToken;

  @Test
  public void testIntentRecognition(){
    String test = "flip a coin";
    WitResponse witRe = testGetter.getWitResponse("Bearer " + witToken,test);
    //Check that the correct intent is extracted from the above String by wit.ai.
    assertEquals("coin-flip",witRe.getEntities().getIntent()[0].getValue());

    test = "Give me a movie quote";
    witRe = testGetter.getWitResponse("Bearer " + witToken,test);
    assertEquals("movie-quote", witRe.getEntities().getIntent()[0].getValue());
  }

  @Test
  //Ensure the fields we expect to be populated are not null when we get a WitResponse back from a wit.ai request
  public void testFieldsNotNull(){

    String test = "flip a coin";
    WitResponse witRe = testGetter.getWitResponse("Bearer " + witToken,test);
    assertNotNull(witRe.getEntities().getIntent());

    test = "Give me a movie quote";
    witRe = testGetter.getWitResponse("Bearer " + witToken,test);
    assertNotNull(witRe.getEntities().getIntent());
  }

}
