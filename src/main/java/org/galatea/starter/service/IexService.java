package org.galatea.starter.service;

import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.IexHistoricalPrices;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * A layer for transformation, aggregation, and business required when retrieving data from IEX.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IexService {

  @NonNull
  private IexClient iexClient;

  @NonNull
  private IexClientToken iexClientToken;



  /**
   * Get all stock symbols from IEX.
   *
   * @return a list of all Stock Symbols from IEX.
   */
  public List<IexSymbol> getAllSymbols() {
    return iexClient.getAllSymbols();
  }

  /**
   * Get the last traded price for each Symbol that is passed in.
   *
   * @param symbols the list of symbols to get a last traded price for.
   * @return a list of last traded price objects for each Symbol that is passed in.
   */
  public List<IexLastTradedPrice> getLastTradedPriceForSymbols(final List<String> symbols) {
    if (CollectionUtils.isEmpty(symbols)) {
      return Collections.emptyList();
    } else {
      return iexClient.getLastTradedPriceForSymbols(symbols.toArray(new String[0]));
    }
  }


  /**
   * Server side get historical prices for a specific symbol passed in,
   * on a specific date and optionally for a specific range.
   *
   * @param symbol symbol to get historical prices for.
   * @param range range of prices to get
   * @param date date for price to get
   *
   * @return a List of IexHistoricalPrices objects for the given symbol,
   *         from the given date until the given date plus range
   */
  public List<IexHistoricalPrices> getHistoricalPrices(
      final String symbol,
      final String range,
      final String date,
      final String token) {
    // Add API Token form IEX_TOKEN env variable
    // pass it along to the client (passed as input for now)
    if (symbol == null || token == null) {
      return Collections.emptyList();
    } else if (symbol.isEmpty() || token.isEmpty()) {
      return Collections.emptyList();
    } else if (range == null || range.isEmpty()) {
      return iexClientToken.getHistoricalPricesBySymbol(symbol, token);
    } else if (date == null || date.isEmpty()) {
      return iexClientToken.getHistoricalPrices(symbol, range, token);
    } else {
      return iexClientToken.getHistoricalPricesByDate(symbol, range, date, token);
    }

  }

}
