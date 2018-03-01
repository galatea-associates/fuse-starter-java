package org.galatea.starter.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;

import org.galatea.starter.domain.TradeAgreement;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Log
@Component
public class SettlementFileParser {

  @NonNull
  private final String delimiter;

  @NonNull
  private final ObjectMapper mapper;

  /**
   * Parse the input file into trade agreements, split by delimiter, skipping incorrectly formatted
   * agreements.
   */
  public List<TradeAgreement> parseTradeAgreements(File file) throws IOException {
    return Files.lines(file.toPath()).parallel()
        .map(line -> line.split(delimiter)).flatMap(Arrays::stream)
        .filter(line -> !line.trim().isEmpty())
        .map(this::parseTradeAgreement)
        .filter(Optional::isPresent).map(Optional::get)
        .collect(Collectors.toList());
  }

  private Optional<TradeAgreement> parseTradeAgreement(String line) {
    try {
      return Optional.ofNullable(mapper.readValue(line, TradeAgreement.class));
    } catch (IOException e) {
      log.error("Unable to parse Trade Agreement from line: {}", line);
      return Optional.empty();
    }
  }
}
