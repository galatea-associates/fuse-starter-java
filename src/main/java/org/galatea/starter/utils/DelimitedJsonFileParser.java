package org.galatea.starter.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts delimiter split JSON into POJOs, skipping and logging any incorrectly formatted input.
 */
@RequiredArgsConstructor
@Slf4j
@Log
public class DelimitedJsonFileParser {

  @NonNull
  private final String delimiter;

  @NonNull
  private final ObjectMapper mapper;

  /**
   * Utilises Java 8 try-with-resources and parallel streams to process multiple lines of the file
   * simultaneously
   */
  public <T> List<T> parseFile(File file, Class<T> target) throws IOException {
    try (Stream<String> lines = Files.lines(file.toPath())) {
      return parseLines(lines.parallel(), target);
    }
  }

  private <T> List<T> parseLines(Stream<String> delimitedLines, Class<T> target) {
    return splitByDelimiter(delimitedLines)
        .map(line -> parseLine(line, target))
        .filter(Optional::isPresent).map(Optional::get)
        .collect(Collectors.toList());
  }

  private Stream<String> splitByDelimiter(Stream<String> lines) {
    return lines.map(line -> line.split(delimiter))
        .flatMap(Arrays::stream)
        .filter(line -> !line.trim().isEmpty());
  }

  private <T> Optional<T> parseLine(String line, Class<T> target) {
    try {
      return Optional.ofNullable(mapper.readValue(line, target));
    } catch (IOException e) {
      log.error("Unable to parse object of type {} from line: {}", target.getSimpleName(), line);
      return Optional.empty();
    }
  }
}
