package org.galatea.starter.service;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@RequiredArgsConstructor
@Slf4j
@Category(org.galatea.starter.IntegrationTestCategory.class)
@SpringBootTest


public class PricesServiceTest extends ASpringTest {

  @Autowired
  private PriceService priceService;
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8090);

  @Test
  public void wireMockTest() throws Exception {

    //Load JSON file and convert to string for stubbing AlphaVantage response
    ClassLoader classLoader = getClass().getClassLoader();

    //Fields to pass into Mock Price Service
    String stock = "MSFT";
    String days = "6";

    //Given: this initial test reference data
    Collection<StockPrices> testStockPrices = createTestCase();

    //When: this URL Path is called, then: mockAlphaResponse should be generated
    stubFor(WireMock.get(urlPathMatching("/query"))
        .withQueryParam("function", equalTo("TIME_SERIES_DAILY_ADJUSTED"))
        .withQueryParam("apikey", equalTo("Q4XJ9KJWS5A109C6"))
        .withQueryParam("symbol", equalTo(stock))
        .withQueryParam("outputsize", equalTo("compact"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBodyFile("WireMockTest.json")
            .withStatus(200)));

    //Call priceService and compare the filtered response to the testStockPrices
    Collection<StockPrices> filteredPrices = priceService.getPricesByStock(stock, days);
    log.info("filteredPrices: {}", filteredPrices);
    assertThat(filteredPrices, is(testStockPrices));
  }

  private Collection<StockPrices> createTestCase () throws ParseException {

    Collection<StockPrices> mockTestCase = new ArrayList<StockPrices>();

    StockPricesBuilder builder1 = StockPrices.builder();
    Date date1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-06-28 20:00:00");
    builder1.date(date1);
    builder1.open(134.5700);
    builder1.high(134.6000);
    builder1.low(133.1558);
    builder1.close(133.9600);
    builder1.adjustedClose(133.9600);
    builder1.volume(30042969);
    builder1.dividendAmount(0.0000);
    builder1.splitCoefficient(1.0000);
    builder1.stock("MSFT");

    StockPricesBuilder builder2 = StockPrices.builder();
    Date date2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-06-27 20:00:00");
    builder2.date(date2);
    builder2.open(134.1400);
    builder2.high(134.7100);
    builder2.low(133.5100);
    builder2.close(134.1500);
    builder2.adjustedClose(134.1500);
    builder2.volume(16557482);
    builder2.dividendAmount(0.0000);
    builder2.splitCoefficient(1.0000);
    builder2.stock("MSFT");

    StockPricesBuilder builder3 = StockPrices.builder();
    Date date3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-06-26 20:00:00");
    builder3.date(date3);
    builder3.open(134.3500);
    builder3.high(135.7400);
    builder3.low(133.6000);
    builder3.close(133.9300);
    builder3.adjustedClose(133.9300);
    builder3.volume(23657745);
    builder3.dividendAmount(0.0000);
    builder3.splitCoefficient(1.0000);
    builder3.stock("MSFT");

    StockPricesBuilder builder4 = StockPrices.builder();
    Date date4 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-06-25 20:00:00");
    builder4.date(date4);
    builder4.open(137.2500);
    builder4.high(137.5900);
    builder4.low(132.7300);
    builder4.close(133.4300);
    builder4.adjustedClose(133.4300);
    builder4.volume(33327420);
    builder4.dividendAmount(0.0000);
    builder4.splitCoefficient(1.0000);
    builder4.stock("MSFT");

    StockPricesBuilder builder5 = StockPrices.builder();
    Date date5 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-06-24 20:00:00");
    builder5.date(date5);
    builder5.open(137.0000);
    builder5.high(138.4000);
    builder5.low(137.0000);
    builder5.close(137.7800);
    builder5.adjustedClose(137.7800);
    builder5.volume(20628841);
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
    log.info ("mockTestCase: {}", mockTestCase);

    return mockTestCase;
  }
}
