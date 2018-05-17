package org.galatea.starter.service;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.Quote;
import org.galatea.starter.domain.wit.WitResponse;
import org.galatea.starter.restclient.QuoteGetter;
import org.galatea.starter.restclient.WitGetter;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
@Log
@Service
public class HalService {

  private static final String REC_READING =
      "https://docs.google.com/spreadsheets/d/1rxtbvuoMvKRdAbgIUKuis-8c5Pdyptvg03m23hikOIM/";

  @NonNull
  QuoteGetter quoteGetter;

  @NonNull
  WitGetter witGetter;


  private static final String DERP = "derp!";

  /**
   * Process the text from GET command into the appropriate command
   *
   * @param text the full text from the GET command. wit.ai will break this down
   * @return the result of executing the command with the given parameters
   */
  public String processText(String text) {
    /* Send raw text to wit.ai */
    WitResponse witResponse = witGetter.getWitResponse("Bearer KGPXCMYTIUAJAWE7R4IVBBL7OTE7L7UE", text);

    switch (witResponse.getEntities().getIntent()[0].getValue()) {
      case "coin-flip":
        return coinFlip();
      case "num-galateans":
        return getNumGalateans().toString();
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
   * @return Map<String, Integer> where String is the location and Integer is the number of workers
   */
  public Map<String, Integer> getNumGalateans() {
    Map<String, Integer> map = new HashMap<>();
    map.put("Florida", 6);
    map.put("London", 13);
    map.put("Boston", 50);
    map.put("NorthCarolina", 5);

    return map;
  }

  /**
   * Get the recommended reading list
   * @return link to the recommended reading list
   */
  public String getRecReading() {
    return REC_READING;
  }


  /**
   * Get a movie quote
   * @return movie quote string
   */
  public String getMovieQuote() {
    Quote[] quote = quoteGetter.getQuote();
    return "Quote: " + quote[0].getQuoteText() + ", from: " + quote[0].getAuthor();
  }

  /**
   * Get derp
   * @return "derp"
   */
  public String getDerp() {
    return DERP;
  }

  /**
   * Helper method used so we can test the coinFlip() without mocking the Random class
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
