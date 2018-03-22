package org.galatea.starter.entrypoint;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;

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

  public static final String HAL_PATH = "/hal";

  /**
   * Retrieve the result of a coin flip
   */
  // @GetMapping to link http GET request to this method
  @GetMapping(value = HAL_PATH + "/coin_flip", produces = {MediaType.TEXT_PLAIN_VALUE})
  public String coinFlip() {
    Random randomNum = new Random();
    int result = randomNum.nextInt(2);

    if (result == 0) {
      return "Tails";
    } else {
      return "Heads";
    }
  }

  /**
   * Retrieve the number of Galateans
   */
  // @GetMapping to link http GET request to this method
  @GetMapping(value = HAL_PATH + "/num_galateans", produces = {MediaType.TEXT_PLAIN_VALUE})
  public String numGalateans() {
    return "There's one important one!";
  }

  /**
   * Retrieve link to the recommended reading list
   */
  // @GetMapping to link http GET request to this method
  @GetMapping(value = HAL_PATH + "/rec_reading", produces = {MediaType.TEXT_HTML_VALUE})
  public String recReading() {
    return "https://docs.google.com/spreadsheets/d/1rxtbvuoMvKRdAbgIUKuis-8c5Pdyptvg03m23hikOIM/";
  }
}
