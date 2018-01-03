package org.galatea.starter.service.client;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.FxRateResponse;
import org.joda.money.CurrencyUnit;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.galatea.starter.Utilities.getJsonFromFile;
import static org.junit.Assert.assertEquals;

/**
 * This test is based upon https://stackoverflow.com/a/45643183
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "client.fx-rate=http://api.fixer.io")
@ContextConfiguration(initializers = IFxRestClientTest.RandomPortInitializer.class)
@EnableFeignClients(clients = IFxRestClient.class)
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class IFxRestClientTest {

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(
            wireMockConfig().dynamicPort()
    );

    @Autowired
    public IFxRestClient restClient;

    @Test
    public void testGetRate() throws Exception {
        stubFor(get(urlEqualTo("/latest?base=GBP&symbols=USD"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getJsonFromFile("FxRateResponse/Correct_FX_Response.json"))));

        FxRateResponse response = restClient.getRate("GBP");

        assertEquals(response.getExchangeRate(), BigDecimal.valueOf(1.3467));
        assertEquals(response.getBaseCurrency(), CurrencyUnit.GBP);
        assertEquals(response.getValidOn(), LocalDate.parse("2017-11-30"));
    }

    public static class RandomPortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            // Without this Feign would go to api.fixer.io instead of localhost
            TestPropertySourceUtils
                    .addInlinedPropertiesToEnvironment(applicationContext,
                            "client.fx-rate=" + "http://localhost:" + wireMockRule.port());
        }
    }
}