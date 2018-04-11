package org.galatea.starter.entrypoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
@Category(org.galatea.starter.IntegrationTestCategory.class)
@SpringBootTest
public class HalRestControllerIntegrationTest extends ASpringTest {

  @Value("${cloud-foundry.dev}")
  private String FuseCfHostName;

  @Test
  public void testCoinFlip() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      fuseHostName = FuseCfHostName;
    }

    System.out.println(FuseCfHostName);

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
      fuseHostName = FuseCfHostName;
    }

    FuseServer fuseServer = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder())
        .target(FuseServer.class, fuseHostName);

    String expResult = "";

    String halResponse = fuseServer.halEndpoint("num-galateans");
    log.info("Number of Galateans response: {}", halResponse);

    assertEquals(expResult, halResponse);
  }

  @Test
  public void testRecReading() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      fuseHostName = FuseCfHostName;
    }

    FuseServer fuseServer = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder())
        .target(FuseServer.class, fuseHostName);

    String expResult = "";

    String halResponse = fuseServer.halEndpoint("rec-reading");
    log.info("Recommended Reading response: {}", halResponse);

    assertEquals(expResult, halResponse);
  }

  @Test
  public void testMovieQuote() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      fuseHostName = FuseCfHostName;
    }

    FuseServer fuseServer = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder())
        .target(FuseServer.class, fuseHostName);

    String expResult = "";

    String halResponse = fuseServer.halEndpoint("movie-quote");
    log.info("Movie Quote response: {}", halResponse);

    assertEquals(expResult, halResponse);
  }

  @Test
  public void testDerp() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      fuseHostName = FuseCfHostName;
    }

    FuseServer fuseServer = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder())
        .target(FuseServer.class, fuseHostName);

    String expResult = "derp!";

    String halResponse = fuseServer.halEndpoint("derp");
    log.info("Derp response: {}", halResponse);

    assertEquals(expResult, halResponse);
  }

  interface FuseServer {

    @RequestLine("GET /hal?text={rawText}")
    String halEndpoint(@Param("rawText") String text);
  }
}
