package org.galatea.starter.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;

import org.galatea.starter.domain.TradeAgreement;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
  private final SettlementFileParser settlementFileParser;

  public Collection<File> processFiles(Collection<File> files) {
    Collection<File> processed = new ArrayList<>();

    for (File file : files) {
      try {
        List<TradeAgreement> agreements = settlementFileParser.parseTradeAgreements(file);
        log.info("Handling agreements {}", agreements);

        Set<Long> missionIds = settlementService.spawnMissions(agreements);
        log.info("Created missions {}", missionIds);

        processed.add(file);
      } catch (IOException exception) {
        log.error("Unable to parse file {}", file.getName());
      }
    }

    return processed;
  }

}
