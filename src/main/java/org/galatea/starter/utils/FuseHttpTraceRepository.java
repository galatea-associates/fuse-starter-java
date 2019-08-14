package org.galatea.starter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;

@RequiredArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class FuseHttpTraceRepository extends InMemoryHttpTraceRepository {

  private final ObjectMapper objectMapper;

  @Override
  public void add(final HttpTrace trace) {
    // HttpTrace has no toString, nor do its inner classes...
    try {
      log.info("Adding trace info: {}", objectMapper.writeValueAsString(trace));
    } catch (JsonProcessingException e) {
      log.warn("Error logging trace info: ", e);
    }
    super.add(trace);
  }
}
