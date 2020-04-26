package org.galatea.starter.entrypoint;


import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.response.Response;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.MessageTranslationConfig;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessages;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.testutils.TestDataGenerator;
import org.galatea.starter.utils.translation.ITranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;

@RequiredArgsConstructor
@Slf4j
// Entire spring application context is loaded so the classes around FuseHttpTraceFilter are present and requests can be handled by SettlementRestController
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// Import Beans from Configuration, enabling them to be Autowired
@Import({MessageTranslationConfig.class})
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class FuseHttpTraceFilterTest extends ASpringTest {

  @Autowired
  ITranslator<TradeAgreementMessages, List<TradeAgreement>> tradeAgreementTranslator;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private SettlementService mockSettlementService;

  private ObjectMapper objectMapper;

  private JacksonTester<TradeAgreementMessages> agreementJsonTester;

  private JacksonTester<List<Long>> missionIdJsonTester;

  private static final Long MISSION_ID_1 = 1091L;

  @LocalServerPort
  private int port;

  @Before
  public void setup() {
    objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, objectMapper);
    RestAssuredMockMvc.webAppContextSetup(context);
    RestAssured.port = port;
  }

  @Test
  @FileParameters(value = "src/test/resources/testSettleAgreement.data",
      mapper = JsonTestFileMapper.class)
  public void testSettleAgreement_JSON(final String agreementJson,
      final String expectedMissionIdJson) throws Exception {
    TradeAgreement expectedAgreement = TradeAgreement.builder().instrument("IBM")
        .internalParty("INT-1").externalParty("EXT-1").buySell("B").qty(100d).build();

    log.info("Agreement json to post {}", agreementJson);

    List<Long> expectedMissionIds = missionIdJsonTester.parse(expectedMissionIdJson).getObject();

    List<String> expectedResponseJsonList = expectedMissionIds.stream()
        .map(id -> "/settlementEngine/mission/" + id).collect(Collectors.toList());

    log.info("Expected json response {}", expectedResponseJsonList);

    TradeAgreementMessages agreementMessages = agreementJsonTester.parse(agreementJson).getObject();
    log.info("Agreement objects that the service will expect {}", agreementMessages);

    given(this.mockSettlementService.spawnMissions(singletonList(expectedAgreement)))
        .willReturn(Sets.newTreeSet(expectedMissionIds));

    Response response =
        RestAssured.given()
            .log().ifValidationFails()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(agreementJson)
            .when()
            .post("/settlementEngine?requestId=1234")
            .then()
            .extract().response();

    verifyHeadersPresent(response);
  }

  @Test
  public void testSettleAgreement_XML() throws Exception {

    TradeAgreementMessages messages = TradeAgreementMessages.builder().agreement(
        TradeAgreementMessage.builder().instrument("IBM").internalParty("INT-1")
            .externalParty("EXT-1").buySell("B").qty(100d).build())
        .build();

    JAXBContext context = JAXBContext.newInstance(TradeAgreementMessages.class);
    Marshaller m = context.createMarshaller();
    StringWriter writer = new StringWriter();
    m.marshal(messages, writer);
    String xml = writer.toString();

    given(this.mockSettlementService.spawnMissions(toTradeAgreements(messages)))
        .willReturn(Sets.newTreeSet(singletonList(1L)));

    Response response =
        RestAssured.given()
            .accept(MediaType.APPLICATION_XML_VALUE)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .body(xml)
            .when()
            .post("/settlementEngine?requestId=1234")
            .then()
            .extract().response();

    verifyHeadersPresent(response);
  }

  private List<TradeAgreement> toTradeAgreements(TradeAgreementMessages messages) {
    return tradeAgreementTranslator.translate(messages);
  }

  @Test
  public void testGetMissionFound_JSON() {
    SettlementMission mission = TestDataGenerator.defaultSettlementMissionData().build();
    log.info("Test mission: {}", mission);

    given(this.mockSettlementService.findMission(MISSION_ID_1))
        .willReturn(Optional.of(mission));

    Response response =
        RestAssured.given().
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            get("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234").
            then().
            extract().response();

    verifyHeadersPresent(response);
  }

  @Test
  public void testGetMissionFound_XML() {
    SettlementMission mission = TestDataGenerator.defaultSettlementMissionData().build();
    log.info("Test mission: {}", mission);

    given(this.mockSettlementService.findMission(MISSION_ID_1))
        .willReturn(Optional.of(mission));

    Response response =
        RestAssured.given()
            .log().ifValidationFails()
            .accept(MediaType.APPLICATION_XML_VALUE)
            .when()
            .get("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
            .then()
            .extract().response();

    verifyHeadersPresent(response);
  }

  @Test
  public void testGetMissionNotFound() {
    BDDMockito.given(this.mockSettlementService.findMission(MISSION_ID_1))
        .willReturn(Optional.empty());

    Response response =
        RestAssured.given()
            .log().ifValidationFails()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
            .then()
            .extract().response();

    verifyHeadersPresent(response);
  }

  @Test
  public void testGetMissionsFound_JSON() {
    SettlementMission mission1 = TestDataGenerator.defaultSettlementMissionData()
        .id(1L).build();
    SettlementMission mission2 = TestDataGenerator.defaultSettlementMissionData()
        .id(2L).build();

    BDDMockito.given(this.mockSettlementService.findMissions(Arrays.asList(1L, 2L)))
        .willReturn(Arrays.asList(mission1, mission2));

    Response response =
        RestAssured.given()
            .log().ifValidationFails()
            .when()
            .get("/settlementEngine/missions?ids=1,2&format=json&requestId=1234")
            .then()
            .extract().response();

    verifyHeadersPresent(response);
  }

  @Test
  public void testGetMissionsFound_XML() {
    SettlementMission mission1 = TestDataGenerator.defaultSettlementMissionData()
        .id(1L).build();
    SettlementMission mission2 = TestDataGenerator.defaultSettlementMissionData()
        .id(2L).build();

    BDDMockito.given(this.mockSettlementService.findMissions(Arrays.asList(1L, 2L)))
        .willReturn(Arrays.asList(mission1, mission2));

    Response response =
        RestAssured.given()
            .log().ifValidationFails()
            .when()
            .get("/settlementEngine/missions?ids=1,2&format=xml&requestId=1234")
            .then()
            .extract().response();

    verifyHeadersPresent(response);
  }

  @Test
  public void testGetMissionsFound_CSV() {
    SettlementMission mission1 = SettlementMission.builder()
        .id(1L).instrument("ABC").externalParty("EXT-1").depot("DEPOT-1").direction("REC")
        .qty(100.0).version(0L).build();
    SettlementMission mission2 = SettlementMission.builder()
        .id(2L).instrument("ABC").externalParty("EXT-1").depot("DEPOT-1").direction("REC")
        .qty(100.0).version(0L).build();

    BDDMockito.given(this.mockSettlementService.findMissions(Arrays.asList(1L, 2L)))
        .willReturn(Arrays.asList(mission1, mission2));

    Response response =
        RestAssured.given()
            .log().ifValidationFails()
            .when()
            .get("/settlementEngine/missions?ids=1,2&format=csv&requestId=1234")
            .then()
            .extract().response();

    verifyHeadersPresent(response);
  }

  @Test
  public void testGetMissionsFound_XLSX() {
    SettlementMission mission1 = SettlementMission.builder()
        .id(1L).instrument("ABC").externalParty("EXT-1").depot("DEPOT-1").direction("REC")
        .qty(100.0).version(0L).build();
    SettlementMission mission2 = SettlementMission.builder()
        .id(2L).instrument("ABC").externalParty("EXT-1").depot("DEPOT-1").direction("REC")
        .qty(100.0).version(0L).build();

    BDDMockito.given(this.mockSettlementService.findMissions(Arrays.asList(1L, 2L)))
        .willReturn(Arrays.asList(mission1, mission2));

    Response response =
        RestAssured.given()
            .log().ifValidationFails()
            .when()
            .get("/settlementEngine/missions?ids=1,2&format=xlsx&requestId=1234")
            .then()
            .extract().response();

    verifyHeadersPresent(response);
  }

  /**
   * Verifies required audit fields are present
   */
  private void verifyHeadersPresent(Response response) {
    Headers allHeaders = response.getHeaders();
    assertThat(allHeaders.getValue("requestReceivedTime"), not(isEmptyOrNullString()));
    assertThat(allHeaders.getValue("requestElapsedTimeMillis"), not(isEmptyOrNullString()));
    assertThat(allHeaders.getValue("externalQueryId"), not(isEmptyOrNullString()));
    assertThat(allHeaders.getValue("internalQueryId"), not(isEmptyOrNullString()));
  }
}