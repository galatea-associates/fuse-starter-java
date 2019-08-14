package org.galatea.starter.utils;

import java.util.Random;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
@ToString
@EqualsAndHashCode
public class Tracer {

  public static final String INTERNAL_REQUEST_ID = "internal-request-id";
  public static final String EXTERNAL_REQUEST_ID = "external-request-id";

  private static final Random QUERY_ID_GENERATOR = new Random();

  private Tracer() {}

  /**
   * Sets the externally provided request id in the trace info and in MDC for inclusion in log
   * messages.
   *
   * @param externalRequestId The externally provided request id
   */
  public static void setExternalRequestId(final String externalRequestId) {
    log.debug("External request id: {}", externalRequestId);

    // And add to MDC so it will show up in the logs
    // The key used here must align with the key defined in the logging config's log-pattern
    MDC.put(EXTERNAL_REQUEST_ID, externalRequestId + " - ");
  }

  public static void setInternalRequestId() {
    // generate the internal request Id
    // we want positive numbers only, so use nextInt(upperBound)
    String internallyGeneratedId =
        Integer.toString(QUERY_ID_GENERATOR.nextInt(Integer.MAX_VALUE));

    log.debug("Created internal request id: {}", internallyGeneratedId);

    // And add to MDC so it will show up in the logs
    // The key used here must align with the key defined in the logging
    // config's log-pattern
    MDC.put(INTERNAL_REQUEST_ID, internallyGeneratedId + " - ");
  }
}
