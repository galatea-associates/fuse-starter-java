package org.galatea.starter.service;

import java.util.Random;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.galatea.starter.domain.WitAiEntity;
import org.galatea.starter.domain.WitAiResolvedEntity;
import org.galatea.starter.domain.WitAiResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Log
@Service
public class HalService {

  @NonNull
  private WitAiService witAiService;

  @NonNull
  private TimeService timeService;

  private static final String IM_SORRY_DAVE = "I'm sorry Dave, I'm afraid I can't do that.";

  /* We need to have this constant so sonar qube doesn't complain */
  private static final String DERP = "derp!";

  // Used for coinFlip
  private final Random randomNum = new Random();

  /**
   * Query our Wit.ai NLP app to determine the meaning of a user's query, and then process handle
   * the intent(s) of that query.
   *
   * @param query user's query.
   * @return a response to return to the user.
   */
  public String queryForMeaningAndProcessText(final String query) {
    WitAiResponse witAiResponse = witAiService.queryWitAi(query);
    return processWitAiIntent(witAiResponse);
  }

  /**
   * Process the text from GET command into the appropriate command.
   *
   * @param witAiResponse the
   * @return the result of executing the command with the given parameters
   */
  public String processWitAiIntent(final WitAiResponse witAiResponse) {

    if (witAiResponse == null || CollectionUtils.isEmpty(witAiResponse.getIntents())) {
      // Wit.ai app was not able to determine any intents, or we did not get a response from wit.ai
      log.warn("wit.ai unreachable or unable to determine intents.");
      return IM_SORRY_DAVE;
    }

    // Get the name of the intent that our wit.ai app has the highest confidence in.
    final String name = witAiResponse.getIntents().get(0).getName();

    switch (name) {
      case "timeAtLocation":
        return timeAtLocation(witAiResponse);
      case "coinFlip":
        return coinFlip();
      case "derp":
        return getDerp();
      default:
        // Wit.ai was able to determine intents, but we do not have handling for the given intent.
        log.warn("Intent [{}] returned from wit.ai, but we don't have any handling for it", name);
        return IM_SORRY_DAVE;
    }
  }

  /**
   * Get the current time for a given location (contained in the wit.ai response).
   *
   * @param witAiResponse response from wit.ai containing intents and entities from user's
   *     query.
   * @return a sentence indicating what time it is in the given location.
   */
  public String timeAtLocation(final WitAiResponse witAiResponse) {

    // Get the wit$location:location entity that our wit.ai app is most confident in (This will be
    // the place that the user has specified in their text - eg. Boston)
    WitAiEntity topEntity =
        witAiService.getTopEntityForEntityType(witAiResponse, "wit$location:location");

    // Get the resolve entity for that location that our wit.ai app is most confident in (this will
    // contain extensive location data for the most likely location that the user is referring to
    // - eg. Boston, MA not Boston, Lincolnshire.
    WitAiResolvedEntity witAiResolvedEntityWrapper = witAiService.getTopResolvedEntity(topEntity);

    // The timezone (eg. America/New_York) is contained in the location data from wit.ai. Look up
    // the current time for that timezone.
    String currentTimeForTimezone =
        timeService.getCurrentTimeForTimezone(witAiResolvedEntityWrapper.getTimezone());

    // Format the response
    return "It is currently " + currentTimeForTimezone + " in " + topEntity.getBody();
  }

  /**
   * Flip a coin.
   *
   * @return "Heads" or "Tails"
   */
  public String coinFlip() {
    if (coinFlipRand() == Coin.TAILS) {
      return "Tails";
    } else {
      return "Heads";
    }
  }

  /**
   * Get derp.
   *
   * @return "derp"
   */
  public String getDerp() {
    return DERP;
  }

  /**
   * Helper method used so we can test the coinFlip() without mocking the Random class.
   *
   * @return Coin.HEADS or Coin.TAILS
   */
  protected Coin coinFlipRand() {
    int flip = randomNum.nextInt(2);

    if (flip == 0) {
      return Coin.TAILS;
    } else {
      return Coin.HEADS;
    }
  }

  protected enum Coin {
    TAILS,
    HEADS
  }

}
