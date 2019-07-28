package org.galatea.starter.service;

import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.IEXLastTradedPrice;
import org.galatea.starter.domain.IEXStockSymbol;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class IEXClientTest extends ASpringTest {

    @Autowired
    private IEXClient iexClient;

    @Test
    public void testGetStockSymbols() {
        List<IEXStockSymbol> stockSymbols = iexClient.getStockSymbols();

        log.info("Result: {}", stockSymbols);
        Assert.assertNotNull(stockSymbols);
        Assert.assertTrue(stockSymbols.size() > 0);
    }

    @Test
    public void testGetLastTradedPriceForSymbolsSingleStock() {
        final String[] symbolsToGetLastTradedPriceFor = new String[]{"FB"};

        List<IEXLastTradedPrice> lastTradedPriceForSymbols = iexClient.getLastTradedPriceForSymbols(symbolsToGetLastTradedPriceFor);

        log.info("Result: {}", lastTradedPriceForSymbols);
        Assert.assertNotNull(lastTradedPriceForSymbols);
        Assert.assertEquals(1, lastTradedPriceForSymbols.size());
    }

    @Test
    public void testGetLastTradedPriceForSymbolsMultipleStocks() {
        final String[] symbolsToGetLastTradedPriceFor = new String[]{"FB", "SNAP", "AIG+"};

        List<IEXLastTradedPrice> lastTradedPriceForSymbols = iexClient.getLastTradedPriceForSymbols(symbolsToGetLastTradedPriceFor);

        log.info("Result: {}", lastTradedPriceForSymbols);
        Assert.assertNotNull(lastTradedPriceForSymbols);
        Assert.assertEquals(3, lastTradedPriceForSymbols.size());
    }

}