package org.galatea.starter.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.WorldTimeApiResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Log
@Service
public class WorldTimeService {

  @NonNull
  private WorldTimeClient worldTimeClient;

  // Produces in the format of "Wed, Dec 22 2021 04:24 PM"
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("EEE, MMM dd yyyy h:mm a");

  /**
   * Get the current time for the given timezone from the World Time API (see WorldTimeClient).
   *
   * @param timezone the timezone to return the current time for.
   * @return A string of the current time (see DATE_TIME_FORMATTER field for format).
   */
  public String getCurrentTimeForTimezone(final String timezone) {
    WorldTimeApiResponse worldTimeApiResponse = worldTimeClient.getCurrentTimeForTimezone(timezone);
    String datetime = worldTimeApiResponse.getDatetime();
    return LocalDateTime.parse(datetime.substring(0, 22)).format(DATE_TIME_FORMATTER);
  }


}
