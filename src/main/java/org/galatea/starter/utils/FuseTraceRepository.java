
package org.galatea.starter.utils;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.actuate.trace.InMemoryTraceRepository;

import java.util.Map;

@RequiredArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class FuseTraceRepository extends InMemoryTraceRepository {

  /**
   * Adds the information in the current trace to our repository.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void addTraceInfo() {
    Map<String, Map<String, Object>> traceInfo = Tracer.getFlattenedCopyOfTraceInfo();
    log.info("Adding trace info: {}", traceInfo);
    add((Map) traceInfo);
  }
}
