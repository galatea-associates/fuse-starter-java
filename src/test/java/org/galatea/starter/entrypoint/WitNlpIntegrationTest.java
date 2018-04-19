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
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class WitNlpIntegrationTest {

  interface FuseServer {
    @RequestLine("GET /hal?text={rawText}")
    String halEndpoint(@Param("rawText") String text);
  }

  private FuseServer fuseServer;

  @Before
  public void setup(){
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      // TODO: the base URL should probably be moved to a src/test/resources properties file
      fuseHostName = "http://fuse-rest-dev.cfapps.io";
    }
    fuseServer = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder())
        .target(FuseServer.class, fuseHostName);
  }
  /*
  The following tests ensure that when a message is sent to wit.ai that it will return a json
  containing the entities that it extracts, and also that those entities are the ones we would
  expect to be extracted from a given utterance.
  If these tests fail it is likely due to our wit.ai app being trained on new data that causes
  it to misinterpret utterances it used to interpret correctly.
  */
  @Test
  public void TestGetQuoteFull() throws Exception {
    String halResponse = fuseServer.halEndpoint("Give me a quote");
    assertEquals("Quote:",halResponse.substring(0,6));
  }

  @Test
  public void TestCoinFlipFull() throws Exception {
    String halResponse = fuseServer.halEndpoint("flip a coin");
    assertThat(halResponse).isIn("Heads", "Tails");
  }

}
