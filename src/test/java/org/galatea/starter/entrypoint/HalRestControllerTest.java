package org.galatea.starter.entrypoint;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
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


@RequiredArgsConstructor
@Slf4j
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
  public void testHalEndpoint() throws Exception {
    String param = "text";
    String paramVal = "coin-flip";
    String result = "Processed String";

    given(this.mockHalService.processText(paramVal)).willReturn(result);

    this.mvc.perform(
        get("/hal").param(param, paramVal).accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", is(result)));
  }

}
