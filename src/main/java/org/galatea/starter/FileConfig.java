package org.galatea.starter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.galatea.starter.utils.DelimitedJsonFileParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class FileConfig {

  @Bean
  public File inboundDirectory(@Value("${entrypoint.file.directory}") String inputPath) {
    return new File(inputPath);
  }

  @Bean
  public DelimitedJsonFileParser fileParser(ObjectMapper mapper,
      @Value("${entrypoint.file.contentDelimiter}") String delimiter) {
    return new DelimitedJsonFileParser(delimiter, mapper);
  }

}
