package org.galatea.starter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.galatea.starter.entrypoint.file.FilePoller;
import org.galatea.starter.entrypoint.file.FileWatcher;
import org.galatea.starter.entrypoint.file.IFilePoller;
import org.galatea.starter.utils.DelimitedJsonFileParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Handles the creation of our file entrypoint related beans based on values defined in our external
 * configuration (application.yml)
 */
@Configuration
public class FileConfig {

  @Bean
  public FileWatcher fileWatcher(@Value("${entrypoint.file.directory}") String directory)
      throws IOException {
    return new FileWatcher(directory);
  }

  @Bean
  public IFilePoller filePoller(FileWatcher fileWatcher,
      @Value("${entrypoint.file.filePatternRegex}") String fileRegex) {
    return new FilePoller(fileWatcher, fileRegex);
  }

  @Bean
  public DelimitedJsonFileParser fileParser(
      @Value("${entrypoint.file.delimiter}") String delimiter, ObjectMapper mapper) {
    return new DelimitedJsonFileParser(delimiter, mapper);
  }

}
