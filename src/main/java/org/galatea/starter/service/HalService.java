package org.galatea.starter.service;


import java.util.HashMap;
import java.util.Map;
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

  private static final String REC_READING =
      "https://docs.google.com/spreadsheets/d/1rxtbvuoMvKRdAbgIUKuis-8c5Pdyptvg03m23hikOIM/";

  private static final String MOVIE_QUOTE = "This mission is too important for me to allow you to jeopardize it";
  private static final String DERP = "derp!";

  protected enum COIN {
    TAILS,
    HEADS
  }

  public String coinFlip() {
    if (coinFlipRand() == COIN.TAILS) {
      return "Tails";
    } else {
      return "Heads";
    }
  }

  public Map<String, Integer> getNumGalateans() {
    Map<String, Integer> map = new HashMap<>();
    map.put("Florida", 6);
    map.put("London", 13);
    map.put("Boston", 50);
    map.put("NorthCarolina", 5);

    return map;
  }

  public String getRecReading() {
    return REC_READING;
  }

  public String getMovieQuote() {
    return MOVIE_QUOTE;
  }

  public String getDerp() {
    return DERP;
  }

  protected COIN coinFlipRand() {
    Random randomNum = new Random();
    int flip = randomNum.nextInt(2);

    if (flip == 0) {
      return COIN.TAILS;
    } else {
      return COIN.HEADS;
    }
  }
}
