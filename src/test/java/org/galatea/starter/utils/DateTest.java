package org.galatea.starter.utils;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class DateTest {

  @Test
  public void testDates() {
    // 1.
    // What is UTC time?

    // From Wikipedia:
    // Coordinated Universal Time (or UTC) is the primary time standard by which the world
    // regulates clocks and time. It is within about 1 second of mean solar time at 0° longitude,
    // and is not adjusted for daylight saving time. It is effectively a successor to
    // Greenwich Mean Time (GMT).

    // 2.
    // UTC time is generally suffixed with "Z"

    // 3.
    // Other time zones are represented as an offset to UTC time.
    // Example, if the UTC time is:  2020-07-17T20:35:27.49Z
    // Then the time in Boston is:   2020-07-17T16:35:27.49-04:00
    // because in July, EDT (Eastern Daylight Time) is four hours behind UTC
    // (this will change when we move to EST (Eastern Standard Time) and are then five hours
    // behind UTC)

    // 4. let's play with time

    // Instant gets you the UTC time
    Instant now = Instant.now();

    // when using Slf4j by default you get a timestamp formatted by
    // DateTimeFormatter.ISO_OFFSET_DATE_TIME (which you'll see again below)
    log.info("now = {}", now);

    // ZonedDateTime represents a date and time in a specific timezone
    // Let's associate now with the timezone, which we already know is UTC
    ZonedDateTime zonedNow = ZonedDateTime.ofInstant(now, ZoneId.of("UTC"));

    // now use this static method to explicitly get a formatted timestamp
    // notice it should be the same as the first log statement
    // 2015-08-13T09:41:43.263Z
    String timestamp = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(zonedNow);
    log.info("timestamp = {}", timestamp);

    // Now let's take the timestamp and turn it back into a ZonedDateTime
    ZonedDateTime zonedFromTimeStamp = ZonedDateTime.parse(timestamp);

    // now turn it back into an Instant
    // This is a special case where we could have done this Instant.parse(timestamp) to get
    // the Instant immediately because we were working in UTC.
    // In general, however, it's recommended to use ZonedDateTime.parse()
    // to avoid parsing errors, and then convert it back to an Instant by calling .toInstant()
    Instant instantFromZonedDateTimeTimeStamp = zonedFromTimeStamp.toInstant();

    // now we should have the same Instant that we started with
    assertEquals(instantFromZonedDateTimeTimeStamp, now);

    // now what happens if we want to convert to another timezone?
    // we'll find the system default, so it should be in your timezone
    ZonedDateTime zonedTimestampInSystemTimeZone = zonedFromTimeStamp.withZoneSameInstant(ZoneId.systemDefault());

    // now let's output it
    String timestampInSystemTimeZone = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(zonedTimestampInSystemTimeZone);
    log.info("timestampInSystemTimeZone = {}", timestampInSystemTimeZone);

    // finally make sure we can got the same Instant back
    // here is an example Instant.parse() doesn't work, because timestamp is not in UTC
    Instant instantFromTimestampInSystemTimeZone = ZonedDateTime.parse(timestampInSystemTimeZone).toInstant();
    assertEquals(now, instantFromTimestampInSystemTimeZone);
  }
}
