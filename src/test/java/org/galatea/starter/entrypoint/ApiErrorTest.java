package org.galatea.starter.entrypoint;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.galatea.starter.MvcConfig.APPLICATION_EXCEL;
import static org.galatea.starter.MvcConfig.TEXT_CSV;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.MessageTranslationConfig;
import org.galatea.starter.MvcConfig;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.entrypoint.exception.EntityNotFoundException;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.testutils.XlsxComparator;
import org.galatea.starter.utils.http.converter.ApiErrorCsvConverter;
import org.galatea.starter.utils.http.converter.SettlementMissionXlsxConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;

@Slf4j
@Import({MessageTranslationConfig.class})
@RunWith(JUnitParamsRunner.class)
public class ApiErrorTest extends ASpringTest {

  @Value("${mvc.settleMissionPath}")
  private String settleMissionPath;

  @Value("${mvc.getMissionPath}")
  private String getMissionPath;

  @Value("${mvc.getMissionsPath}")
  private String getMissionsPath;

  @Value("${mvc.deleteMissionPath}")
  private String deleteMissionPath;

  @Value("${mvc.updateMissionPath}")
  private String updateMissionPath;

  @MockBean
  private SettlementService mockSettlementService;

  @Autowired
  private SettlementRestController settlementRestController;

  @Before
  public void setup() {
    Map<String, MediaType> mediaTypes = new HashMap<>();
    mediaTypes.put("json", MediaType.APPLICATION_JSON);
    mediaTypes.put("xml", MediaType.APPLICATION_XML);
    mediaTypes.put("csv", TEXT_CSV);
    mediaTypes.put("xlsx", APPLICATION_EXCEL);

    ParameterContentNegotiationStrategy parameterContentNegotiationStrategy =
        new ParameterContentNegotiationStrategy(mediaTypes);

    ContentNegotiationManager manager =
        new ContentNegotiationManager(parameterContentNegotiationStrategy);

    RestAssuredMockMvc.standaloneSetup(
        MockMvcBuilders.standaloneSetup(settlementRestController).
            addPlaceholderValue("mvc.settleMissionPath", settleMissionPath).
            addPlaceholderValue("mvc.deleteMissionPath", deleteMissionPath).
            addPlaceholderValue("mvc.updateMissionPath", updateMissionPath).
            addPlaceholderValue("mvc.getMissionsPath", getMissionsPath).
            addPlaceholderValue("mvc.getMissionPath", getMissionPath).
            setContentNegotiationManager(manager).
            setMessageConverters(new MappingJackson2HttpMessageConverter(),
                new Jaxb2RootElementHttpMessageConverter(),
                new ApiErrorCsvConverter(),
//                new SettlementMissionCsvConverter(),
                new SettlementMissionXlsxConverter()).
            setControllerAdvice(new RestExceptionHandler()));
  }

  @Test
  public void testApiErrorResponse_JSON() {
    List<Long> ids = new ArrayList<>();
    ids.add(1L);
    ids.add(2L);

    Sets.SetView<Long> missingMissions = Sets.difference(new HashSet<>(ids),
        Collections.emptySet());

    // cause EntityNotFoundException on GET /settlementEngine/missions?ids={}
    BDDMockito.given(mockSettlementService.findMissions(ids))
        .willThrow(new EntityNotFoundException(SettlementMission.class, missingMissions));

    EntityNotFoundException exception = new EntityNotFoundException(SettlementMission.class, missingMissions);

    given()
        .log().ifValidationFails()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get("/settlementEngine/missions?ids=1,2&format=json&requestId=1234")
        .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("status", is(HttpStatus.NOT_FOUND.name()))
        .body("message", is(exception.toString()));
  }

  @Test
  public void testApiErrorResponse_XML() {
    List<Long> ids = new ArrayList<>();
    ids.add(1L);
    ids.add(2L);

    Sets.SetView<Long> missingMissions = Sets.difference(new HashSet<>(ids),
        Collections.emptySet());

    // cause EntityNotFoundException on GET /settlementEngine/missions?ids={}
    BDDMockito.given(mockSettlementService.findMissions(ids))
        .willThrow(new EntityNotFoundException(SettlementMission.class, missingMissions));

    EntityNotFoundException exception = new EntityNotFoundException(SettlementMission.class, missingMissions);

    given()
        .log().ifValidationFails()
        .contentType(MediaType.APPLICATION_XML_VALUE)
        .when()
        .get("/settlementEngine/missions?ids=1,2&format=xml&requestId=1234")
        .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body(hasXPath("(//status)[1]", is(HttpStatus.NOT_FOUND.name())))
        .body(hasXPath("(//message)[1]", is(exception.toString())));
  }

  @SneakyThrows
  @Test
  public void testApiErrorResponse_CSV() {
    List<Long> ids = new ArrayList<>();
    ids.add(1L);
    ids.add(2L);

    Sets.SetView<Long> missingMissions = Sets.difference(new HashSet<>(ids),
        Collections.emptySet());

    // cause EntityNotFoundException on GET /settlementEngine/missions?ids={}
    BDDMockito.given(mockSettlementService.findMissions(ids))
        .willThrow(new EntityNotFoundException(SettlementMission.class, missingMissions));

    String expectedCsv = readData("ApiError.csv");

    given()
        .log().ifValidationFails()
        .contentType(MvcConfig.TEXT_CSV_VALUE)
        .when()
        .get("/settlementEngine/missions?ids=1,2&format=csv&requestId=1234")
        .then()
        .log().ifValidationFails()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body(is(expectedCsv));
  }

  @SneakyThrows
  @Test
  public void testApiErrorResponse_XLSX() {
    List<Long> ids = new ArrayList<>();
    ids.add(1L);
    ids.add(2L);

    Sets.SetView<Long> missingMissions = Sets.difference(new HashSet<>(ids),
        Collections.emptySet());

    // cause EntityNotFoundException on GET /settlementEngine/missions?ids={}
    BDDMockito.given(mockSettlementService.findMissions(ids))
        .willThrow(new EntityNotFoundException(SettlementMission.class, missingMissions));

    byte[] expectedXlsx = readBytes("SettlementMissions.xlsx");

    MockMvcResponse response =
        given()
            .log().ifValidationFails()
            .contentType(MvcConfig.APPLICATION_EXCEL_VALUE)
            .when()
            .get("/settlementEngine/missions?ids=1,2&format=xlsx&requestId=1234")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .response();

    // Directly comparing the spreadsheet bytes fails even when the expected spreadsheet appears to
    // be an exact copy of the actual result, so instead compare the spreadsheet contents logically
    assertTrue(XlsxComparator.equals(expectedXlsx, response.asByteArray()));
  }

  @Configuration
  @Import(SettlementRestController.class)
  @ConditionalOnNotWebApplication
  static class PropertyConfig {

    @Bean
    PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
      PropertyPlaceholderConfigurer propertyPlaceholderConfigurer =
          new PropertyPlaceholderConfigurer();
      propertyPlaceholderConfigurer.setLocation(new ClassPathResource("application.properties"));
      return propertyPlaceholderConfigurer;
    }
  }
}
