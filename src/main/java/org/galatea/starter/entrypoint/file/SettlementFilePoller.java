package org.galatea.starter.entrypoint.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
@Log
@Slf4j
@Component
public class SettlementFilePoller {

  @NonNull
  private final SettlementFileProcessingManager fileProcessingManager;

  @NonNull
  private final IFilePoller filePoller;

  @Scheduled(fixedRate = 10000)
  public void processFiles() {
    try {
      Collection<File> candidateFiles = filePoller.poll();
      log.debug("Found input files for processing {}", candidateFiles);

      fileProcessingManager.processFiles(candidateFiles);
    } catch (IOException exception) {
      log.error("Error scanning directory for files", exception);
    }
  }

}
