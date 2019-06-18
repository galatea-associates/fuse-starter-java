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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@RequiredArgsConstructor
@Slf4j
@Category(org.galatea.starter.IntegrationTestCategory.class)
@SpringBootTest


public class PriceRestControllerIntegrationTest {

  @Autowired
  private PricesClient pricesclient;


  @Test
    public void processStock() {

      PricesClient fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
          .target(PricesClient.class, "http://localhost:8080/prices?stock=&days=105");

      String halResponse = fuseServer.halEndpoint("coin-flip");
      log.info("Coin flip response: {}", halResponse);

      assertThat(halResponse).isIn("Heads", "Tails");
    }


    interface PricesClient {

      @RequestLine("GET /hal?text={rawText}")
      String halEndpoint(@Param("rawText") String text);
    }

}
