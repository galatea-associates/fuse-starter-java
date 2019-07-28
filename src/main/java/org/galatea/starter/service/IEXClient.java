package org.galatea.starter.service;

import org.galatea.starter.domain.IEXLastTradedPrice;
import org.galatea.starter.domain.IEXStockSymbol;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * A Feign Declarative REST Client to access endpoints from the Free and Open IEX API to get market data.
 * See https://iextrading.com/developer/docs/
 */
@FeignClient(name = "IEX", url = "${spring.rest.iexBasePath}")
public interface IEXClient {

    /**
     * Get a list of all stocks supported by IEX. See https://iextrading.com/developer/docs/#symbols. As of July 2019
     * this returns almost 9,000 symbols, so maybe don't call it in a loop.
     *
     * @return a list of all of the stock symbols supported by IEX.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/ref-data/symbols")
    List<IEXStockSymbol> getStockSymbols();

    /**
     * Get the last traded price for each stock symbol passed in. See https://iextrading.com/developer/docs/#last.
     *
     * @param symbols stock symbols to get last traded price for.
     * @return a list of the last traded price for each of the symbols passed in.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/tops/last")
    List<IEXLastTradedPrice> getLastTradedPriceForSymbols(@RequestParam("symbols") String[] symbols);

}
