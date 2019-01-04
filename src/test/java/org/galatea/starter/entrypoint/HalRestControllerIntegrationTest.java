package org.galatea.starter.entrypoint;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@RequiredArgsConstructor
@Slf4j
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class HalRestControllerIntegrationTest {

  @Value("${fuse-host.url}")
  private String FuseHostName;

  private static final Integer WIREMOCK_PORT = 8089;
  private static final String WIREMORK_URL =  String.format("http://localhost:%d/", WIREMOCK_PORT) ;

  @ClassRule
  public static WireMockClassRule wireMockRule =
          new WireMockClassRule(options().port(WIREMOCK_PORT).usingFilesUnderClasspath("wiremock"));
  @Rule
  public WireMockClassRule instanceRule = wireMockRule;

  @Test
  public void testCoinFlip() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      fuseHostName = FuseHostName == null ? WIREMORK_URL : FuseHostName;
    }

    FuseServer fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(FuseServer.class, fuseHostName);

    String halResponse = fuseServer.halEndpoint("coin-flip");
    log.info("Coin flip response: {}", halResponse);

    assertThat(halResponse).isIn("Heads", "Tails");
  }

  @Test
  public void testDerp() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      fuseHostName = FuseHostName == null ? WIREMORK_URL : FuseHostName;
    }

    FuseServer fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
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
