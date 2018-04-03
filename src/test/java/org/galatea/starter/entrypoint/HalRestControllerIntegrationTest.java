package org.galatea.starter.entrypoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class HalRestControllerIntegrationTest {

  @Test
  public void testCoinFlip() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      // TODO: the base URL should probably be moved to a src/test/resources properties file
      fuseHostName = "http://fuse-rest-dev.cfapps.io";
    }

    FuseServer fuseServer = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder())
        .target(FuseServer.class, fuseHostName);

    String halResponse = fuseServer.halEndpoint("coin-flip");
    log.info("Coin flip response: {}", halResponse);

    assertThat(halResponse).isIn("Heads", "Tails");
  }

  @Test
  public void testNumGalateans() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      // TODO: the base URL should probably be moved to a src/test/resources properties file
      fuseHostName = "http://fuse-rest-dev.cfapps.io";
    }

    FuseServer fuseServer = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder())
        .target(FuseServer.class, fuseHostName);

    Map<String, Integer> map = new HashMap<>();
    map.put("Florida", 6);
    map.put("London", 13);
    map.put("Boston", 50);
    map.put("NorthCarolina", 5);

    String halResponse = fuseServer.halEndpoint("num-galateans");
    log.info("Number of Galateans response: {}", halResponse);

    assertEquals(halResponse, map.toString());
  }

  @Test
  public void testRecReading() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      // TODO: the base URL should probably be moved to a src/test/resources properties file
      fuseHostName = "http://fuse-rest-dev.cfapps.io";
    }

    FuseServer fuseServer = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder())
        .target(FuseServer.class, fuseHostName);

    String expResult = "https://docs.google.com/spreadsheets/d/1rxtbvuoMvKRdAbgIUKuis-8c5Pdyptvg03m23hikOIM/";

    String halResponse = fuseServer.halEndpoint("rec-reading");
    log.info("Recommended Reading response: {}", halResponse);

    assertEquals(halResponse, expResult);
  }

  @Test
  public void testMovieQuote() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      // TODO: the base URL should probably be moved to a src/test/resources properties file
      fuseHostName = "http://fuse-rest-dev.cfapps.io";
    }

    FuseServer fuseServer = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder())
        .target(FuseServer.class, fuseHostName);

    String expResult = "This mission is too important for me to allow you to jeopardize it";

    String halResponse = fuseServer.halEndpoint("movie-quote");
    log.info("Movie Quote response: {}", halResponse);

    assertEquals(halResponse, expResult);
  }

  @Test
  public void testDerp() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      // TODO: the base URL should probably be moved to a src/test/resources properties file
      fuseHostName = "http://fuse-rest-dev.cfapps.io";
    }

    FuseServer fuseServer = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder())
        .target(FuseServer.class, fuseHostName);

    String expResult = "derp!";

    String halResponse = fuseServer.halEndpoint("derp");
    log.info("Derp response: {}", halResponse);

    assertEquals(halResponse, expResult);
  }

  interface FuseServer {

    @RequestLine("GET /hal?text={rawText}")
    String halEndpoint(@Param("rawText") String text);
  }
}
