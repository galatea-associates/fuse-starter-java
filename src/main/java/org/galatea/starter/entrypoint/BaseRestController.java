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
  protected static final char ENCODER_SEPARATOR = '_';

  /** 
   * Encodes specified message, to avoid any log forging.
   */
  protected String encode(String message){

    String encodedMessage = message.replace( '\n', ENCODER_SEPARATOR ).replace( '\r', ENCODER_SEPARATOR )
      .replace( '\t', ENCODER_SEPARATOR );
    
    encodedMessage = ESAPI.encoder().encodeForHTML( encodedMessage );

    return encodedMessage;
  }


  /**
   * Adds the specified requestId to the context for this request (if not null).
   */
  protected void processRequestId(String requestId) {
    if (requestId != null) {
      String encodedRequestId = encode(requestId);
      log.info("Request received with id: {}", encodedRequestId);
      Tracer.setExternalRequestId(encodedRequestId);
    }
  }
}
