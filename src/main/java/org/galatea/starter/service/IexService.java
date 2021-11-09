package org.galatea.starter.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.apache.commons.lang3.StringUtils;
import org.galatea.starter.domain.IexHistoricalPrices;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * A layer for transformation, aggregation, and business required when retrieving data from IEX.
 */
@Slf4j
@Log
@Service
@RequiredArgsConstructor
public class IexService {

  @NonNull
  private IexClient iexClient;

  @NonNull
  private IexClientToken iexClientToken;

  /**
   * Read API key from src/main/resources/key.txt
   *
   * @return a String representation of the API token.
   *
   */
  private String getToken() {
    String token = null;
    Charset charset = Charset.forName("US-ASCII");
    try (BufferedReader reader = Files.newBufferedReader(
        Path.of("src/main/resources/key.txt"),
        charset)) {
      token = reader.readLine();
    } catch (IOException x) {
      log.error("IOException: ", x);
    }
    return token;
  }



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
  @Cacheable(cacheNames = "historical")
  public List<IexHistoricalPrices> getHistoricalPrices(
      final String symbol,
      final String range,
      final String date) {
    log.info("Query did not hit the cache");
    String token = getToken();
    if (StringUtils.isBlank(symbol) || StringUtils.isBlank(token)) {
      return Collections.emptyList();
    } else if (StringUtils.isBlank(range)) {
      return iexClientToken.getHistoricalPricesBySymbol(symbol, token);
    } else if (StringUtils.isBlank(date)) {
      return iexClientToken.getHistoricalPrices(symbol, range, token);
    } else {
      return iexClientToken.getHistoricalPricesByDate(symbol, range, date, token);
    }

  }

}
