package org.galatea.starter.entrypoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@RequiredArgsConstructor
@Slf4j
@Category(org.galatea.starter.IntegrationTestCategory.class)
@SpringBootTest
public class HalRestControllerIntegrationTest extends ASpringTest {

  @Value("${fuse-host.url}")
  private String FuseHostName;

  @Test
  public void testCoinFlip() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      fuseHostName = FuseHostName;
    }

    FuseServer fuseServer =
        Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
            .target(FuseServer.class, fuseHostName);

    String halResponse = fuseServer.halEndpoint("coin-flip");
    log.info("Coin flip response: {}", halResponse);

    assertThat(halResponse).isIn("Heads", "Tails");
  }

  @Test
  public void testDerp() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      fuseHostName = FuseHostName;
    }

    FuseServer fuseServer =
        Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
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
