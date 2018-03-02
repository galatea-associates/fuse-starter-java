package org.galatea.starter.entrypoint;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.file.DirectoryScanner;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;

/**
 * Polls a configuration defined directory every 10 seconds for new files, passing the identified
 * files into SettlementFileProcessor#processFile(File) .
 */
@RequiredArgsConstructor
@Component
public class SettlementFilePoller {

  @NonNull
  private File inboundDirectory;

  @Bean
  public IntegrationFlow filePollingFlow(MessageSource<File> fileReader) {
    return IntegrationFlows.from(fileReader, c -> c.poller(Pollers.fixedDelay(10000)))
        .handle("settlementFileProcessor", "processFile")
        .get();
  }

  @Bean
  public FileReadingMessageSource fileReadingMessageSource(DirectoryScanner directoryScanner) {
    FileReadingMessageSource source = new FileReadingMessageSource();
    source.setDirectory(this.inboundDirectory);
    source.setScanner(directoryScanner);
    source.setAutoCreateDirectory(true);
    return source;
  }

  @Bean
  public DirectoryScanner directoryScanner(
      @Value("${entrypoint.file.filePatternRegex}") String regex) {
    DirectoryScanner scanner = new RecursiveDirectoryScanner();
    scanner.setFilter(new CompositeFileListFilter<>(Arrays.asList(
        new AcceptOnceFileListFilter<>(),
        new RegexPatternFileListFilter(regex))
    ));
    return scanner;
  }

}
