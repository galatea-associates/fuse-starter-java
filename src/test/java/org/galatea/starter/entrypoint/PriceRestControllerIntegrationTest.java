package org.galatea.starter.entrypoint;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.internal.StockPrices;
import org.galatea.starter.domain.internal.StockPrices.StockPricesBuilder;
import org.galatea.starter.service.PriceService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@RequiredArgsConstructor
@Slf4j
@Category(org.galatea.starter.IntegrationTestCategory.class)
@SpringBootTest


public class PriceRestControllerIntegrationTest extends ASpringTest {

  @Autowired
  private PriceService mockPriceService;
  @Rule
  public WireMockRule wireMockRule = new WireMockRule();


  @Test
  public void wireMockTest() throws Exception {

    //Load JSON file and convert to string for stubbing AlphaVantage response
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("WireMockTest.json").getFile());
    JsonNode MockAlphaResponse = new ObjectMapper().readTree(testFile);
    String mockAlphaResponse = MockAlphaResponse.toString();

    //Fields to pass into Mock Price Service
    String stock = "MSFT";
    String days = "6";

    //Given: this initial test reference data
    Collection<StockPrices> testStockPrices = createTestCase();

    //When: this URL Path is called, then: mockAlphaResponse should be generated
    stubFor(WireMock.get(urlPathEqualTo("/query?function=TIME_SERIES_DAILY_ADJUSTED&apikey=Q4XJ9KJWS5A109C6&symbol=MSFT&outputsize=compact"))
        .willReturn(aResponse()
            .withBody(mockAlphaResponse)
            .withStatus(200)));

    //Call mockPriceService and compare the filtered response to the testStockPrices
    Collection<StockPrices> filteredPrices = mockPriceService.getPricesByStock(stock, days);
    log.info("filteredPrices: {}", filteredPrices);
    assertThat(filteredPrices, is(testStockPrices));
  }


  private Collection<StockPrices> createTestCase () throws ParseException {

    Collection<StockPrices> mockTestCase = new ArrayList<StockPrices>();

    StockPricesBuilder builder1 = StockPrices.builder();
    Date date1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-07-01 20:00:00");
    builder1.date(date1);
    builder1.open(136.6300);
    builder1.high(136.7000);
    builder1.low(134.9700);
    builder1.close(135.6800);
    builder1.adjustedClose(135.6800);
    builder1.volume(22606027);
    builder1.dividendAmount(0.0000);
    builder1.splitCoefficient(1.0000);
    builder1.stock("MSFT");

    StockPricesBuilder builder2 = StockPrices.builder();
    Date date2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-06-28 20:00:00");
    builder2.date(date2);
    builder2.open(134.5700);
    builder2.high(136.7000);
    builder2.low(133.1558);
    builder2.close(133.9600);
    builder2.adjustedClose(133.9600);
    builder2.volume(30042969);
    builder2.dividendAmount(0.0000);
    builder2.splitCoefficient(1.0000);
    builder2.stock("MSFT");

    StockPricesBuilder builder3 = StockPrices.builder();
    Date date3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-06-27 20:00:00");
    builder3.date(date3);
    builder3.open(134.1400);
    builder3.high(134.7100);
    builder3.low(133.5100);
    builder3.close(134.1500);
    builder3.adjustedClose(134.1500);
    builder3.volume(16557482);
    builder3.dividendAmount(0.0000);
    builder3.splitCoefficient(1.0000);
    builder3.stock("MSFT");

    StockPricesBuilder builder4 = StockPrices.builder();
    Date date4 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-06-26 20:00:00");
    builder4.date(date4);
    builder4.open(134.3500);
    builder4.high(135.7400);
    builder4.low(133.6000);
    builder4.close(133.9300);
    builder4.adjustedClose(133.9300);
    builder4.volume(23657745);
    builder4.dividendAmount(0.0000);
    builder4.splitCoefficient(1.0000);
    builder4.stock("MSFT");

    StockPricesBuilder builder5 = StockPrices.builder();
    Date date5 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-06-25 20:00:00");
    builder5.date(date5);
    builder5.open(137.2500);
    builder5.high(137.5900);
    builder5.low(132.7300);
    builder5.close(133.4300);
    builder5.adjustedClose(133.4300);
    builder5.volume(33327420);
    builder5.dividendAmount(0.0000);
    builder5.splitCoefficient(1.0000);
    builder5.stock("MSFT");

    StockPrices data1 = builder1.build();
    StockPrices data2 = builder2.build();
    StockPrices data3 = builder3.build();
    StockPrices data4 = builder4.build();
    StockPrices data5 = builder5.build();

    mockTestCase.add(data1);
    mockTestCase.add(data2);
    mockTestCase.add(data3);
    mockTestCase.add(data4);
    mockTestCase.add(data5);

    return mockTestCase;
  }
}
