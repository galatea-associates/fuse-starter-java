package org.galatea.starter.entrypoint;

import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.service.PriceService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

//
//  @Test
//  public void testPriceEndpoint() throws Exception {
//    String param1 = "stock";
//    String paramVal1 = "MSFT";
//    String param2 = "days";
//    String paramVal2 = "104";
//    ClassLoader classLoader = getClass().getClassLoader();
//    File file = new File(classLoader.getResource("msft_104_test.json").getFile());
//
//    given(this.mockPriceService.getPricesByStock(paramVal1, paramVal2)).willReturn(AlphaPrices(file));
//
//    this.mvc.perform(
//        get("/prices")
//            .param(param1, paramVal1)
//            .param(param2, "104")
//            .accept(MediaType.APPLICATION_JSON))
//        .andExpect(jsonPath("$", is(file))); //$: root object
//  }

}
