package org.galatea.starter.entrypoint;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;


@RequiredArgsConstructor
@Slf4j
// We need to do a full application start up for this one, since we want the feign clients to be instantiated.
// It's possible we could do a narrower slice of beans, but it wouldn't save that much test run time.
@SpringBootTest
// this gives us the MockMvc variable
@AutoConfigureMockMvc
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class IexRestControllerTest extends ASpringTest {

    @ClassRule
    // there are other ways to enable WireMock, including the spring boot @EnableWireMock annotation, but the ClassRule is pretty simple,
    // and follows the pattern we use in ASpringTest.
    public static WireMockClassRule wiremock = new WireMockClassRule(WireMockSpring.options()
            // this port needs to match the spring.rest.iexBasePath url in application.yml for the test profile.
            // you can configure WireMock to use a random port, and bind that port to a system variable as an alternative.
            .port(9938)
            // point at the json mappings and stubs held in files.  There are a variety of schemes for representing the WireMock stubs.
            .usingFilesUnderClasspath("wiremock"));

    @Autowired
    private MockMvc mvc;

    @Test
    public void testGetSymbolsEndpoint() throws Exception {

        MvcResult result = this.mvc.perform(
                // note that we were are testing the fuse REST end point here, not the IEX end point.
                // the fuse end point in turn calls the IEX end point, which is WireMocked for this test.
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/iex/symbols")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                // some simple validations, in practice I would expect these to be much more comprehensive.
                .andExpect(jsonPath("$[0].symbol", is("A")))
                .andExpect(jsonPath("$[1].symbol", is("AA")))
                .andExpect(jsonPath("$[2].symbol", is("AAAU")))
                .andReturn();
    }

    @Test
    public void testGetLastTradedPrice() throws Exception {

        MvcResult result = this.mvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/iex/lastTradedPrice?symbols=FB")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol", is("FB")))
                .andExpect(jsonPath("$[0].price").value(new BigDecimal("182.305")))
                .andReturn();
    }
}
