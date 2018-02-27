package org.galatea.starter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.galatea.starter.entrypoint.SettlementFileListener;
import org.galatea.starter.service.SettlementFileParser;
import org.galatea.starter.service.SettlementFileProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class FileListenerConfig {

  // inject the value from application properties (see application.yml)
  @Value("${entrypoint.file.directory}")
  private String listenerDirectoryPath;

  @Value("${entrypoint.file.filePatternRegex}")
  private String filePatternRegex;

  @Value("${entrypoint.file.delimiter}")
  private String delimiter;

  @Bean
  public SettlementFileListener settlementFileListener(SettlementFileProcessor fileProcessor) {
    File directory = new File(listenerDirectoryPath);
    return new SettlementFileListener(directory, filePatternRegex, fileProcessor);
  }

  @Bean
  public SettlementFileParser settlementFileParser(ObjectMapper mapper) {
    return new SettlementFileParser(delimiter, mapper);
  }

}
