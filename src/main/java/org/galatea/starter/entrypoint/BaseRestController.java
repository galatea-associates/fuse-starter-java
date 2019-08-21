package org.galatea.starter.entrypoint;

import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.slf4j.MDC;

/**
 * Provides base functionality shared by all Fuse REST controllers.
 */
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
public abstract class BaseRestController {

  public static final String EXTERNAL_REQUEST_ID = "external-request-id";

  /**
   * Adds the specified requestId to the context for this request (if not null).
   */
  protected void processRequestId(final String requestId) {
    if (requestId != null) {
      log.info("Request received with id: {}", requestId);

      // And add to MDC so it will show up in the logs
      // The key used here must align with the key defined in the logging config's log-pattern
      MDC.put(EXTERNAL_REQUEST_ID, requestId + " - ");
    }
  }
}
