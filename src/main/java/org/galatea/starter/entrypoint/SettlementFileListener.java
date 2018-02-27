package org.galatea.starter.entrypoint;

import static org.apache.commons.io.FileUtils.listFiles;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.galatea.starter.service.SettlementFileProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log
@Slf4j
@Component
public class SettlementFileListener {

  @NonNull
  private final File directory;

  @NonNull
  private final String filePatternRegex;

  @NonNull
  private final SettlementFileProcessor fileProcessor;

  private final Set<File> processedFiles = new HashSet<>();

  @Scheduled(fixedRate = 5000)
  public Collection<File> processFilesInDirectory() {
    if (!directory.isDirectory()) {
      log.error("File input directory {} does not exist", directory.getName());
      return Collections.emptyList();
    }

    Collection<File> candidateFiles = findCandidateFiles();
    log.info("Found input files for processing {}", candidateFiles);

    return processFiles(candidateFiles);
  }

  private Collection<File> findCandidateFiles() {
    Collection<File> filesMatchingRegex =
        listFiles(directory,
            new RegexFileFilter(filePatternRegex), null);

    return filesMatchingRegex.stream()
        .filter(file -> !processedFiles.contains(file))
        .collect(Collectors.toList());
  }

  private Collection<File> processFiles(Collection<File> files) {
    Collection<File> processed = fileProcessor.processFiles(files);
    processedFiles.addAll(processed);
    return processed;
  }
}
