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

  @Value("${entrypoint.file.directory}")
  private String listenerDirectoryPath;

  @Value("${entrypoint.file.filePatternRegex}")
  private String filePatternRegex;

  @Value("${entrypoint.file.delimiter}")
  private String delimiter;

  @Bean
  public SettlementFileListener settlementFileListener(SettlementFileProcessor fileProcessor) {
    return new SettlementFileListener(listenerDirectory(), filePatternRegex, fileProcessor);
  }

  @Bean
  public File listenerDirectory() {
    return new File(listenerDirectoryPath);
  }

  @Bean
  public SettlementFileParser settlementFileParser(ObjectMapper mapper) {
    return new SettlementFileParser(delimiter, mapper);
  }

}
