package org.galatea.starter.entrypoint;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Collection;
import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.internal.StockPrices;
import org.galatea.starter.service.PriceService;
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
@WebMvcTest(PriceRestController.class)
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.



@RunWith(JUnitParamsRunner.class)
public class PriceRestControllerTest extends ASpringTest {

  @Autowired
  private MockMvc mvc;


  @MockBean
  private PriceService mockPriceService;


  @Test
  public void testPriceEndpoint() throws Exception {
    String param1 = "stock";
    String paramVal1 = "tsla";
    String param2 = "days";
    String paramVal2 = "5";
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("TestFile.json").getFile());

    StockPrices result = new ObjectMapper().readValue(testFile, StockPrices.class);


    given(this.mockPriceService.getPricesByStock(paramVal1, paramVal2)).willReturn(
        (Collection<StockPrices>) result);


    this.mvc.perform(
        get("/prices")
            .param(param1, paramVal1)
            .param(param2, "5")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", is(result)));
  }
}
