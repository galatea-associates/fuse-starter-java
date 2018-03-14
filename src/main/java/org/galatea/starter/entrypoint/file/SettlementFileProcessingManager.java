package org.galatea.starter.entrypoint.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages the processing of settlement files.  Handles the reprocessing of files that fail to
 * process.  Not thread safe.
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class SettlementFileProcessingManager {

  @NonNull
  private final SettlementFileProcessor fileProcessor;

  private final List<File> processingQueue = new ArrayList<>();

  public Collection<File> processFiles(final Collection<File> files) {
    log.debug("Adding files to the processing queue {}", files);
    processingQueue.addAll(files);

    return processQueue();
  }

  private Collection<File> processQueue() {
    Collection<File> processed = new ArrayList<>();

    for (File file : processingQueue) {
      log.debug("Processing file {}", file.getName());
      try {
        fileProcessor.processFile(file);
        processed.add(file);
      } catch (IOException exception) {
        log.error("Unable to process file {}", file.getName(), exception);
      }
    }

    processingQueue.removeAll(processed);

    return processed;
  }

}
