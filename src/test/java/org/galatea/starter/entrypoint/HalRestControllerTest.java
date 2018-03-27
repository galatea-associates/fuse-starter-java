package org.galatea.starter.entrypoint;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.HashMap;
import junitparams.JUnitParamsRunner;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.service.HalService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;


@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
// We don't load the entire spring application context for this test.
@WebMvcTest(HalRestController.class)
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class HalRestControllerTest extends ASpringTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private HalService mockHalService;

  @Test
  public void testGetCoinFlip() throws Exception {
    String flip = "Heads";

    when(mockHalService.coinFlip()).thenReturn(flip);

    this.mvc.perform(get("/hal/coin-flip").accept(MediaType.TEXT_PLAIN_VALUE))
        .andExpect(content().string(flip));
  }

  @Test
  public void testGetNumGalateans() throws Exception {
    Integer florida = 6;
    Integer london = 13;
    Integer boston = 50;
    Integer northCarolina = 5;

    Map <String, Integer> map = new HashMap<>();
    map.put("Florida", 6);
    map.put("London", 13);
    map.put("Boston", 50);
    map.put("NorthCarolina", 5);

    when(mockHalService.getNumGalateans()).thenReturn(map);

    this.mvc.perform(
        get("/hal/num-galateans").accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.Florida", is(florida)))
        .andExpect(jsonPath("$.London", is(london)))
        .andExpect(jsonPath("$.Boston", is(boston)))
        .andExpect(jsonPath("$.NorthCarolina", is(northCarolina)));
  }

  @Test
  public void testGetRecReading() throws Exception {
    String recReading =
        "https://docs.google.com/spreadsheets/d/1rxtbvuoMvKRdAbgIUKuis-8c5Pdyptvg03m23hikOIM/";

    when(mockHalService.getRecReading()).thenReturn(recReading);

    this.mvc
        .perform(get("/hal/rec-reading").accept(MediaType.TEXT_HTML_VALUE))
        .andExpect(content().string(recReading));
  }

  @Test
  public void testGetMovieQuote() throws Exception {
    String quote = "This mission is too important for me to allow you to jeopardize it";

    when(mockHalService.getMovieQuote()).thenReturn(quote);

    this.mvc
        .perform(get("/hal/movie-quote").accept(MediaType.TEXT_PLAIN_VALUE))
        .andExpect(content().string(quote));
  }

  @Test
  public void testGetDerp() throws Exception {
    String derp = "derp!";

    when(mockHalService.getDerp()).thenReturn(derp);

    this.mvc.perform(get("/hal/derp").accept(MediaType.TEXT_PLAIN_VALUE))
        .andExpect(content().string(derp));
  }
}
