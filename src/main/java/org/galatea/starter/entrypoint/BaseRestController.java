package org.galatea.starter.entrypoint;

import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.utils.Tracer;

/**
 * Provides base functionality shared by all Fuse REST controllers
 */
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
public abstract class BaseRestController {

  /**
   * Adds the specified requestId to the context for this request (if not null).
   */
  protected void processStock (String stock, String daysToLookBack) {
    if (stock != null) {
      log.info("Request received with stock: {}", stock);
      Tracer.setExternalRequestId(stock);
    }
    if (daysToLookBack != null) {
      log.info("Request received with # of days: 1");
      Tracer.setExternalRequestId(daysToLookBack);

     }
   }
 }
