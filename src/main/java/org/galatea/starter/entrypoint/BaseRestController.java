package org.galatea.starter.entrypoint;

import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.utils.Tracer;
import org.owasp.esapi.ESAPI;
/**
 * Provides base functionality shared by all Fuse REST controllers
 */
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
public abstract class BaseRestController {

  /** 
   * Encodes specified message, to avoid any log forging.
   */
  protected String encode(String message){
    message = message.replace( '\n', '_' ).replace( '\r', '_' )
      .replace( '\t', '_' );
    
    message = ESAPI.encoder().encodeForHTML( message );

    return message;
  }


  /**
   * Adds the specified requestId to the context for this request (if not null).
   */
  protected void processRequestId(String requestId) {
    if (requestId != null) {
      log.info("Request received with id: {}", encode(requestId));
      Tracer.setExternalRequestId(encode(requestId));
    }
  }
}
