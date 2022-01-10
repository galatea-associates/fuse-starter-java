package org.galatea.starter.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Log
@Service
public class TimeService {

  // Produces in the format of "Wed, Dec 22 2021 04:24 PM"
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("EEE, MMM dd yyyy h:mm a");

  /**
   * Get the current time for the given timezone using java.time.ZonedDateTime.
   *
   * @param timezone the timezone to return the current time for.
   * @return A string of the current time (see DATE_TIME_FORMATTER field for format).
   */
  public String getCurrentTimeStringForTimezone(final String timezone) {
    // Assuming this JVM is running in the America/New_York timezone.
    ZonedDateTime zonedNewYork = LocalDateTime.now().atZone(ZoneId.of("America/New_York"));
    return zonedNewYork.withZoneSameInstant(ZoneId.of(timezone)).format(DATE_TIME_FORMATTER);
  }


}
