package org.galatea.starter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class FilePollerConfig {

  @Bean
  public File inboundDirectory(@Value("${entrypoint.file.directory}") String inputPath) {
    return new File(inputPath);
  }

}
