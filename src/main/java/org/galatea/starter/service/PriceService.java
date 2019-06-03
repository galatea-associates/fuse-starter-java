package org.galatea.starter.service;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Log
@Service
public class PriceService {

  /* We need to have this constant so sonar qube doesn't complain */
  private static final String DERP = "derp!";
//  public static String stock;
//  public static String daysToLookBack;

  /**
   * Process the stock from GET command into the appropriate command
   *
   * @param stock from the full text from the GET command. Wit.ai will break this down
   * @param daysToLookBack from the full text from the GET command. Wit.ai will break this down
   * @return the result of executing the command with the given parameters
   */


  public String processStock(String stock, String daysToLookBack) {

//    int daysToLookBack2 = Integer.parseInt(daysToLookBack);
//
//    if (0 < )
    String url = ("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="
        + stock + "&outputsize=full&apikey=Q4XJ9KJWS5A109C6");
    return url;
  }


  //place holder for switch based on # of days requested

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


