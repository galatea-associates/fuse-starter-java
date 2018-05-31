package org.galatea.starter.entrypoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.galatea.starter.TestConfig;
import org.galatea.starter.entrypoint.TestFuseServer;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.RestClientConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;

//This allows us to autowire the beans defined in TestConfig.java
@SpringBootTest(classes = {TestConfig.class})
@Import({FeignAutoConfiguration.class})
@Category(org.galatea.starter.IntegrationTestCategory.class)
@Slf4j
public class HalRestControllerIntegrationTest extends ASpringTest {

  @Value("${fuse-host.url}")
  private String FuseHostName;

  @Autowired
  TestFuseServer fuseServer;

  @Test
  public void testCoinFlip() {

    String halResponse = fuseServer.halEndpoint("coin-flip");
    log.info("Coin flip response: {}", halResponse);

    assertThat(halResponse).isIn("\"Heads\"", "\"Tails\"");
  }

  @Test
  public void testDerp() {

    String expResult = "\"derp!\"";

    String halResponse = fuseServer.halEndpoint("derp");
    log.info("Derp response: {}", halResponse);

    assertEquals(expResult, halResponse);
  }

}
