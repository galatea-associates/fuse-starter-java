package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import junitparams.JUnitParamsRunner;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.RestClientConfig;
import org.galatea.starter.domain.wit.WitResponse;
import org.galatea.starter.restclient.WitGetter;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {RestClientConfig.class},initializers = ConfigFileApplicationContextInitializer.class)
@TestPropertySource(locations = {"classpath:application.yml"})
@ImportAutoConfiguration({RibbonAutoConfiguration.class, FeignRibbonClientAutoConfiguration.class, FeignAutoConfiguration.class})
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
