package org.galatea.starter.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@Slf4j
@SpringBootTest
public class IexSerivceTest extends ASpringTest {

  @Autowired
  private IexService iexService;

  @MockBean
  private IexClient mockIexClient;

  @Test
  public void testGetStockSymbols() {

    List<IexSymbol> mockResponse = new ArrayList<>();
    mockResponse.add(IexSymbol.builder()
        .symbol("SNAP")
        .name("Snapchat")
        .date(new Date())
        .isEnabled(true)
        .type("test")
        .iexId("123")
        .build());

    given(this.mockIexClient.getAllSymbols()).willReturn(mockResponse);

    List<IexSymbol> stockSymbols = iexService.getAllSymbols();

    log.info("Result: {}", stockSymbols);

    Assert.assertEquals(1, stockSymbols.size());
  }

  @Test
  public void testGetLastTradedPriceForSymbolsSingleStock() {

    final List<String> symbolsToGetLastTradedPriceFor = Collections.singletonList("SNAP");
    List<IexLastTradedPrice> mockResponse = new ArrayList<>();
    mockResponse.add(IexLastTradedPrice.builder()
        .symbol("SNAP")
        .price(123L)
        .size(1)
        .time(12908371L)
        .build());

    given(this.mockIexClient.getLastTradedPriceForSymbols(new String[]{"SNAP"}))
        .willReturn(mockResponse);

    List<IexLastTradedPrice> lastTradedPriceForSymbols = iexService
        .getLastTradedPriceForSymbols(symbolsToGetLastTradedPriceFor);

    log.info("Result: {}", lastTradedPriceForSymbols);
    Assert.assertEquals(1, lastTradedPriceForSymbols.size());
    Assert.assertEquals("SNAP", lastTradedPriceForSymbols.get(0).getSymbol());
  }

  @Test
  public void testGetLastTradedPriceForSymbolsMultipleStocks() {
    final List<String> symbolsToGetLastTradedPriceFor = Arrays.asList("FB", "SNAP", "AIG+");

    List<IexLastTradedPrice> mockResponse = new ArrayList<>();
    mockResponse.add(IexLastTradedPrice.builder()
        .symbol("SNAP").price(123L).size(1).time(12908371L).build());
    mockResponse.add(IexLastTradedPrice.builder()
        .symbol("FB").price(456L).size(1).time(12909372L).build());
    mockResponse.add(IexLastTradedPrice.builder()
        .symbol("AIG+").price(678L).size(100).time(123408371L).build());

    given(this.mockIexClient.getLastTradedPriceForSymbols(new String[]{"FB", "SNAP", "AIG+"}))
        .willReturn(mockResponse);

    List<IexLastTradedPrice> lastTradedPriceForSymbols = iexService
        .getLastTradedPriceForSymbols(symbolsToGetLastTradedPriceFor);

    log.info("Result: {}", lastTradedPriceForSymbols);
    Assert.assertEquals(3, lastTradedPriceForSymbols.size());
  }

  @Test
  public void testGetLastTradedPriceForSymbolsNoStocks() {
    final List<String> symbolsToGetLastTradedPriceFor = new ArrayList<>();

    List<IexLastTradedPrice> mockResponse = new ArrayList<>();

    given(this.mockIexClient.getLastTradedPriceForSymbols(new String[0]))
        .willReturn(mockResponse);

    List<IexLastTradedPrice> lastTradedPriceForSymbols = iexService
        .getLastTradedPriceForSymbols(symbolsToGetLastTradedPriceFor);

    log.info("Result: {}", lastTradedPriceForSymbols);
    Assert.assertEquals(0, lastTradedPriceForSymbols.size());

    // The Client should never have been called since we passed an empty list
    Mockito.verify(mockIexClient, never()).getLastTradedPriceForSymbols(any());
  }

}