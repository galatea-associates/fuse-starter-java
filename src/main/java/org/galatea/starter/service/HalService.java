package org.galatea.starter.service;

import java.util.Random;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
@Log
@Service
public class HalService {

  /* We need to have these constants so sonar qube doesn't complain */
  String EMPTY_STRING = "";
  String DERP = "derp!";

  /**
   * Process the text from GET command into the appropriate command
   *
   * @param text the full text from the GET command. Wit.ai will break this down
   * @return the result of executing the command with the given parameters
   */
  public String processText(String text) {
    switch (text) {
      case "coin-flip":
        return coinFlip();
      case "num-galateans":
        return getNumGalateans();
      case "rec-reading":
        return getRecReading();
      case "movie-quote":
        return getMovieQuote();
      case "derp":
        return getDerp();
      default:
        return "Unsupported command";
    }
  }

  /**
   * Flip a coin
   *
   * @return "Heads" or "Tails"
   */
  public String coinFlip() {
    if (coinFlipRand() == COIN.TAILS) {
      return "Tails";
    } else {
      return "Heads";
    }
  }

  /**
   * Get the number of Galateans in each office
   *
   * @return Empty string
   */
  public String getNumGalateans() {
    return EMPTY_STRING;
  }

  /**
   * Get the recommended reading list
   *
   * @return Empty string
   */
  public String getRecReading() {
    return EMPTY_STRING;
  }

  /**
   * Get a movie quote
   *
   * @return Empty string
   */
  public String getMovieQuote() {
    return EMPTY_STRING;
  }

  /**
   * Get derp
   *
   * @return "derp"
   */
  public String getDerp() {
    return DERP;
  }

  /**
   * Helper method used so we can test the coinFlip() without mocking the Random class
   *
   * @return COIN.HEADS or COIN.TAILS
   */
  protected COIN coinFlipRand() {
    Random randomNum = new Random();
    int flip = randomNum.nextInt(2);

    if (flip == 0) {
      return COIN.TAILS;
    } else {
      return COIN.HEADS;
    }
  }

  protected enum COIN {
    TAILS,
    HEADS
  }
}
