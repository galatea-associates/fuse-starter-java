package org.galatea.starter.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class IexClientTest extends ASpringTest {

  @Autowired
  private IexClient iexClient;

  @Test
  public void testGetStockSymbols() {
    List<IexSymbol> stockSymbols = iexClient.getAllSymbols();

    log.info("Result: {}", stockSymbols);
    Assert.assertNotNull(stockSymbols);
    Assert.assertTrue(stockSymbols.size() > 0);
  }

  @Test
  public void testGetLastTradedPriceForSymbolsSingleStock() {
    final String[] symbolsToGetLastTradedPriceFor = new String[]{"FB"};

    List<IexLastTradedPrice> lastTradedPriceForSymbols = iexClient
        .getLastTradedPriceForSymbols(symbolsToGetLastTradedPriceFor);

    log.info("Result: {}", lastTradedPriceForSymbols);
    Assert.assertNotNull(lastTradedPriceForSymbols);
    Assert.assertEquals(1, lastTradedPriceForSymbols.size());
  }

  @Test
  public void testGetLastTradedPriceForSymbolsMultipleStocks() {
    final String[] symbolsToGetLastTradedPriceFor = new String[]{"FB", "SNAP", "AIG+"};

    List<IexLastTradedPrice> lastTradedPriceForSymbols = iexClient
        .getLastTradedPriceForSymbols(symbolsToGetLastTradedPriceFor);

    log.info("Result: {}", lastTradedPriceForSymbols);
    Assert.assertNotNull(lastTradedPriceForSymbols);
    Assert.assertEquals(3, lastTradedPriceForSymbols.size());
  }

}