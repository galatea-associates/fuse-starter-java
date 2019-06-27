package org.galatea.starter.entrypoint;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.internal.StockPrices;
import org.galatea.starter.domain.internal.StockPrices.StockPricesBuilder;
import org.galatea.starter.service.PriceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
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
  ResourceLoader resourceLoader;


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
    JsonNode result = new ObjectMapper().readTree(testFile);

    ArrayList<StockPrices> testStockData = new ArrayList<>();

//    StockMetadataBuilder builder = StockMetadata.builder();
//      builder.processTime("1(ms)");
//      builder.endpoint("price?stock=TSLA&days=6");
//      builder.host("2019-Computer4");
//      builder.responseTime("0(ms)");
//      builder.timeStamp("6/27/19 9:56 AM");
//      StockMetadata metaData = builder.build();

    StockPricesBuilder builder1 = StockPrices.builder();
//      builder1.date(2019-06-21);
    builder1.open(0.0);
    builder1.high(222.18);
    builder1.low(215.5);
    builder1.close(221.86);
    builder1.adjustedClose(221.86);
    builder1.volume(8202078.0);
    builder1.dividendAmount(0.0);
    builder1.splitCoefficient(1.0);
    builder1.stock("tsla");
    StockPrices data1 = builder1.build();

    StockPricesBuilder builder2 = StockPrices.builder();
    builder2.open(0.0);
    builder2.high(226.9);
    builder2.low(216.35);
    builder2.close(219.62);
    builder2.adjustedClose(219.62);
    builder2.volume(1.1863462E7);
    builder2.dividendAmount(0.0);
    builder2.splitCoefficient(1.0);
    builder2.stock("tsla");
    StockPrices data2 = builder1.build();

    StockPricesBuilder builder3 = StockPrices.builder();
    builder3.open(0.0);
    builder3.high(227.7);
    builder3.low(221.06);
    builder3.close(226.43);
    builder3.adjustedClose(226.43);
    builder3.volume(6575135.0);
    builder3.dividendAmount(0.0);
    builder3.splitCoefficient(1.0);
    builder3.stock("tsla");
    StockPrices data3 = builder1.build();

    StockPricesBuilder builder4 = StockPrices.builder();
    builder4.open(0.0);
    builder4.high(234.74);
    builder4.low(222.56);
    builder4.close(224.74);
    builder4.adjustedClose(224.74);
    builder4.volume(1.2715788E7);
    builder4.dividendAmount(0.0);
    builder4.splitCoefficient(1.0);
    builder4.stock("tsla");
    StockPrices data4 = builder1.build();

    testStockData.add(data1);
    testStockData.add(data2);
    testStockData.add(data3);
    testStockData.add(data4);

//    FullResponseBuilder builder5 = FullResponse.builder();
//    builder5.metaData(metaData);
//    builder5.prices(testStockData);
//    FullResponse testFullResponse = builder5.build();


    given(this.mockPriceService.getPricesByStock(paramVal1, paramVal2)).willReturn(
        testStockData);

    this.mvc.perform(
        get("/prices")
            .param(param1, paramVal1)
            .param(param2, paramVal2)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", is(result)));
  }
}