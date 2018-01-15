package org.galatea.starter.service.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.galatea.starter.TestUtilities.getJsonFromFile;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import feign.FeignException;

import org.galatea.starter.domain.FxRateResponse;
import org.joda.money.CurrencyUnit;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * This test is based upon https://stackoverflow.com/a/45643183 It's not really an integration test but
 * can't be run with unit tests due to required set up.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "client.fx-rate=http://api.fixer.io")
@ContextConfiguration(initializers = IFxRestClientTest.RandomPortInitializer.class)
@EnableFeignClients(clients = IFxRestClient.class)
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class IFxRestClientTest {

  @ClassRule
  public static WireMockClassRule wireMockRule =
      new WireMockClassRule(wireMockConfig().dynamicPort());

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Autowired
  public IFxRestClient restClient;

  @Test
  public void testGetRate() throws Exception {
    stubFor(
        get(urlEqualTo("/latest?base=GBP&symbols=USD"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(getJsonFromFile("FxRateResponse/Correct_FX_Response.json"))));

    FxRateResponse response = restClient.getRate("GBP");

    assertEquals(BigDecimal.valueOf(1.3467), response.getExchangeRate());
    assertEquals(CurrencyUnit.GBP, response.getBaseCurrency());
    assertEquals(LocalDate.parse("2017-11-30"), response.getValidOn());
  }

  @Test
  public void testGetRate404() throws Exception {
    expectedException.expect(FeignException.class);

    stubFor(
        get(urlEqualTo("/latest?base=GBP&symbols=USD")).willReturn(aResponse().withStatus(404)));

    FxRateResponse response = restClient.getRate("GBP");
  }

  public static class RandomPortInitializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

      // Without this Feign would go to api.fixer.io instead of localhost
      TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
          applicationContext, "client.fx-rate=" + "http://localhost:" + wireMockRule.port());
    }
  }

  @After
  public void tearDown() {
    WireMock.reset();
  }
}
