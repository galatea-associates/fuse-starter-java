package org.galatea.starter.entrypoint;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.galatea.starter.service.IexService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@RequiredArgsConstructor
@Slf4j
// We don't load the entire spring application context for this test.
@WebMvcTest(IexRestController.class)
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class IexRestControllerTest extends ASpringTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private IexService mockIexService;

  @Test
  public void testGetSymbolsEndpoint() throws Exception {

    List<IexSymbol> mockResponse = new ArrayList<>();
    mockResponse.add(IexSymbol.builder()
        .symbol("SNAP")
        .name("Snapchat")
        .date(new Date())
        .isEnabled(true)
        .type("test")
        .iexId("123")
        .build());

    given(this.mockIexService.getAllSymbols()).willReturn(mockResponse);

    this.mvc.perform(
        get("/iex/symbols").accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }

  @Test
  public void testGetLastTradedPrice() throws Exception {

    List<IexLastTradedPrice> mockResponse = new ArrayList<>();

    mockResponse.add(IexLastTradedPrice.builder()
        .symbol("SNAP")
        .price(123L)
        .size(1)
        .time(12908371L)
        .build());

    given(this.mockIexService.getLastTradedPriceForSymbols(Collections.singletonList("SNAP")))
        .willReturn(mockResponse);

    this.mvc.perform(
        get("/iex/symbols")
            .param("symbols", "SNAP")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }
}
