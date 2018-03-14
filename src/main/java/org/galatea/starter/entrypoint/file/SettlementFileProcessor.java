package org.galatea.starter.entrypoint.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;

import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.DelimitedJsonFileParser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Log
@Slf4j
@Component
public class SettlementFileProcessor {

  @NonNull
  private final SettlementService settlementService;

  @NonNull
  private final DelimitedJsonFileParser fileParser;

  public void processFile(final File file) throws IOException {
    List<TradeAgreement> agreements = fileParser.parseFile(file, TradeAgreement.class);
    log.info("Handling agreements {}", agreements);

    Set<Long> missionIds = settlementService.spawnMissions(agreements);
    log.info("Created missions {}", missionIds);
  }

}
