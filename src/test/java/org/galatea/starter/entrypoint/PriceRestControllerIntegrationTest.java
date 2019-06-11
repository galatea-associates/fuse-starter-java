package org.galatea.starter.entrypoint;


import static org.assertj.core.api.Assertions.assertThat;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@RequiredArgsConstructor
@Slf4j
@Category(org.galatea.starter.IntegrationTestCategory.class)
@SpringBootTest


public class PriceRestControllerIntegrationTest {

    @Value("${fuse-host.url}")
    private String FuseHostName;

    @Test
    public void processStock() {
      String fuseHostName = System.getProperty("fuse.sandbox.url");
      if (fuseHostName == null || fuseHostName.isEmpty()) {
        fuseHostName = FuseHostName;
      }

      PricesClient fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
          .target(PricesClient.class, fuseHostName);

      String halResponse = fuseServer.halEndpoint("coin-flip");
      log.info("Coin flip response: {}", halResponse);

      assertThat(halResponse).isIn("Heads", "Tails");
    }


    interface PricesClient {

      @RequestLine("GET /hal?text={rawText}")
      String halEndpoint(@Param("rawText") String text);
    }

}
