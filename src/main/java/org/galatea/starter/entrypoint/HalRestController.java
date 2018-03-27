package org.galatea.starter.entrypoint;

import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;

import org.galatea.starter.service.HalService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * REST Controller that generates and listens to http endpoints which allow the caller to create
 * Missions from TradeAgreements and query them back out.
 */
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@Validated
@RestController
public class HalRestController {

  @NonNull
  HalService halService;

  /**
   * Retrieve the result of a coin flip from the Hal Service
   */
  // @GetMapping to link http GET request to this method
  @GetMapping(value = "${webservice.halpath}" + "/coin-flip", produces = {
      MediaType.TEXT_PLAIN_VALUE})
  public String coinFlip() {
    return halService.coinFlip();
  }

  /**
   * Retrieve the number of Galateans from the Hal Service
   */
  // @GetMapping to link http GET request to this method
  @GetMapping(value = "${webservice.halpath}" + "/num-galateans", produces = {
      MediaType.APPLICATION_JSON_VALUE})
  public Map<String, Integer> numGalateans() {
    return halService.getNumGalateans();
  }

  /**
   * Retrieve link to the recommended reading list from the Hal Service
   */
  // @GetMapping to link http GET request to this method
  @GetMapping(value = "${webservice.halpath}" + "/rec-reading", produces = {
      MediaType.TEXT_HTML_VALUE})
  public String recReading() {
    return halService.getRecReading();
  }

  /**
   * Retrieve a movie quote from the Hal Service
   */
  // @GetMapping to link http GET request to this method
  @GetMapping(value = "${webservice.halpath}" + "/movie-quote", produces = {
      MediaType.TEXT_PLAIN_VALUE})
  public String movieQuote() {
    return halService.getMovieQuote();
  }

  /**
   * Return derp from the Hal Service
   */
  // @GetMapping to link http GET request to this method
  @GetMapping(value = "${webservice.halpath}" + "/derp", produces = {MediaType.TEXT_PLAIN_VALUE})
  public String derp() {
    return halService.getDerp();
  }
}
