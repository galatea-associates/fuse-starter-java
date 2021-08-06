package org.galatea.starter.entrypoint;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@RequiredArgsConstructor
@Slf4j
// We need to do a full application start up for this one, since we want the feign clients to be instantiated.
// It's possible we could do a narrower slice of beans, but it wouldn't save that much test run time.
@SpringBootTest
// this gives us the MockMvc variable
@AutoConfigureMockMvc
// we previously used WireMockClassRule for consistency with ASpringTest, but when moving to a dynamic port
// to prevent test failures in concurrent builds, the wiremock server was created too late and feign was
// already expecting it to be running somewhere else, resulting in a connection refused
@AutoConfigureWireMock(port = 0, files = "classpath:/wiremock")
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class HalRestControllerTest extends ASpringTest {

  @Autowired
  private MockMvc mvc;

  /*
    Note that we were are testing the fuse REST end points here, not the wit.ai or world time api
    end points. The fuse end point in turn calls the wit.ai and world time apiend points, which are
    both WireMocked for this test.
   */

  @Test
  public void testHalEndpointForTimeAtLocation() throws Exception {
    MvcResult result = this.mvc.perform(
            MockMvcRequestBuilders
                .get("/hal?text=Time in New York")
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().string("It is currently Tue, Dec 21 2021 4:27 PM in New York"))
        .andReturn();
  }

  @Test
  public void testHalEndpointForCoinFlip() throws Exception {
    MvcResult result = this.mvc.perform(
            MockMvcRequestBuilders
                .get("/hal?text=Flip a coin")
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andReturn();

    List<String> expected = Arrays.asList("Heads", "Tails");

    Assert.assertTrue(expected.contains(result.getResponse().getContentAsString()));

  }

}
