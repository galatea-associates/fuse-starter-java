package org.galatea.starter.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.galatea.starter.domain.TradeAgreement;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SettlementFileParserTest {

  private SettlementFileParser fileParser;

  @Before
  public void setUp() {
    String delimiter = "\\|";
    fileParser = new SettlementFileParser(delimiter, new ObjectMapper());
  }

  @Test
  public void correctlyParsesAllValidAgreements() throws Exception {
    File testFile = resourceAsFile("validTradeAgreements.txt");

    List<TradeAgreement> agreements = fileParser.parseTradeAgreements(testFile);

    TradeAgreement firstAgreement = TradeAgreement.builder()
        .instrument("IBM")
        .internalParty("INT-1")
        .externalParty("EXT-1")
        .buySell("B")
        .qty(100D)
        .build();

    TradeAgreement secondAgreement = TradeAgreement.builder()
        .instrument("IBM")
        .internalParty("INT-1")
        .externalParty("EXT-1")
        .buySell("S")
        .qty(100D)
        .build();

    assertTrue(agreements.containsAll(Arrays.asList(firstAgreement, secondAgreement)));

    assertEquals(2, agreements.size());
  }

  @Test
  public void invalidInputAgreementsHandled() throws Exception {
    File testFile = resourceAsFile("invalidTradeAgreements.txt");

    List<TradeAgreement> agreements = fileParser.parseTradeAgreements(testFile);

    assertEquals(2, agreements.size());
  }

  private File resourceAsFile(String filePath) {
    return new File(getClass().getClassLoader().getResource(filePath).getFile());
  }

}