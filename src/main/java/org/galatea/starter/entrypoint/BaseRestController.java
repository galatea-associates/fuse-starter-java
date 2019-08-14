package org.galatea.starter.entrypoint;

import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.utils.Tracer;
import org.springframework.web.util.UriUtils;

/**
 * Provides base functionality shared by all Fuse REST controllers.
 */
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
public abstract class BaseRestController {

  /**
   * Adds the specified requestId to the context for this request (if not null).
   */
  protected void processRequestId(final String requestId) {
    //This is a temporary solution for cleaning special characters from the REST request params
    //It will not fix newlines in the parameters or JSON body being printed by the @Log annotation
    //Proper fix requires version 2.10.0 or higher of the log4j dependencies
    //See Issue #243 for more information.
    if (requestId != null) {
      String cleanedRequestId = null;
      try {
        cleanedRequestId = UriUtils.encode(requestId, "ISO-8859-1");
        log.info("Request received.  Cleaned id: {}", cleanedRequestId);
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
        log.error("Unable to clean requestId, uncleaned version will be used in logs");
        log.info("Request received with id: {}", cleanedRequestId);
        cleanedRequestId = requestId;
      }
      Tracer.setExternalRequestId(cleanedRequestId);
    }
  }
}
