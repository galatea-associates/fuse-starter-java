package org.galatea.starter.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.TradeAgreement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class SettlementFileProcessorTest extends ASpringTest {

  private SettlementFileProcessor fileProcessor;

  @Mock
  private SettlementService settlementService;

  @Mock
  private SettlementFileParser fileParser;

  @Before
  public void setUp() {
    fileProcessor = new SettlementFileProcessor(settlementService, fileParser);
  }

  @Test
  public void parsesTradeAgreements() throws Exception {
    File testFile = new File("anyPath");

    List<TradeAgreement> agreements = Collections.singletonList(TradeAgreement.builder()
        .instrument("IBM")
        .internalParty("INT-1")
        .externalParty("EXT-1")
        .buySell("B")
        .qty(100D)
        .build()
    );

    when(fileParser.parseTradeAgreements(testFile)).thenReturn(agreements);

    fileProcessor.processFiles(Collections.singleton(testFile));

    verify(fileParser).parseTradeAgreements(testFile);
    verify(settlementService).spawnMissions(agreements);
  }

}