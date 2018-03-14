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

@RequiredArgsConstructor
@Slf4j
@Component
public class SettlementFileProcessingManager {

  @NonNull
  private final SettlementFileProcessor fileProcessor;

  private final List<File> processingQueue = new ArrayList<>();

  private final Set<File> processedFiles = new HashSet<>();

  public Collection<File> processFiles(final Collection<File> files) {
    Collection<File> filesToProcess = removeDuplicates(files);

    processingQueue.addAll(filesToProcess);

    return processQueue();
  }

  private Collection<File> removeDuplicates(Collection<File> files) {
    return files.stream().distinct()
        .filter(file -> !processingQueue.contains(file))
        .filter(file -> !processedFiles.contains(file))
        .collect(Collectors.toList());
  }

  private Collection<File> processQueue() {
    Collection<File> processed = new ArrayList<>();

    for (File file : processingQueue) {
      log.debug("Processing file {}", file.getName());
      try {
        fileProcessor.processFile(file);
        processedFiles.add(file);
        processed.add(file);
      } catch (IOException exception) {
        log.error("Unable to process file {}", file.getName(), exception);
      }
    }

    processingQueue.removeAll(processed);

    return processed;
  }

}
