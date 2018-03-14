package org.galatea.starter.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.file.SettlementFileProcessor;
import org.galatea.starter.utils.DelimitedJsonFileParser;
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
  private DelimitedJsonFileParser delimitedJsonFileParser;

  @Before
  public void setUp() {
    fileProcessor = new SettlementFileProcessor(settlementService, delimitedJsonFileParser);
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

    when(delimitedJsonFileParser.parseFile(testFile, TradeAgreement.class)).thenReturn(agreements);

    fileProcessor.processFile(testFile);

    verify(delimitedJsonFileParser).parseFile(testFile, TradeAgreement.class);
    verify(settlementService).spawnMissions(agreements);
  }

}