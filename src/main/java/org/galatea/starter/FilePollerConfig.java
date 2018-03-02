package org.galatea.starter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.galatea.starter.utils.DelimitedJsonFileParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class FilePollerConfig {

  // inject value from application properties (see application.yml)
  @Value("${entrypoint.file.directory}")
  private String inputPath;

  @Value("${entrypoint.file.filePatternRegex}")
  private String filePatternRegex;

  @Value("${entrypoint.file.contentDelimiter}")
  private String inputDelimiter;

  @Bean
  public File inboundDirectory() {
    return new File(inputPath);
  }

  @Bean
  public DelimitedJsonFileParser fileParser(ObjectMapper mapper) {
    return new DelimitedJsonFileParser(inputDelimiter, mapper);
  }

}
