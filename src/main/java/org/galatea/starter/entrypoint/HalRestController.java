package org.galatea.starter.entrypoint;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.service.HalService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller that listens to http endpoints and allows the caller to send text to be
 * processed
 */
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@RestController
public class HalRestController extends BaseRestController {

  @NonNull
  HalService halService;

  /**
   * Send the received text to the HalService to be processed and send the result out
   */
  // @GetMapping to link http GET request to this method
  // @RequestParam to take a parameter from the url
  @GetMapping(value = "${webservice.halpath}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public String halEndpoint(@RequestParam(value = "text") String text,
      @RequestParam(value = "requestId", required = false) String requestId) {
    processRequestId(requestId);
    return halService.processText(text);
  }
}
