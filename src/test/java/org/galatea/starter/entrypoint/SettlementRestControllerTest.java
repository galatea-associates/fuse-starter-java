package org.galatea.starter.entrypoint;


import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.googlecode.protobuf.format.JsonFormat;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.MvcConfig;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessage;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.translation.ITranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode(callSuper = true)
// We don't load the entire spring application context for this test.
@WebMvcTest(SettlementRestController.class)
// Import Beans from Configuration, enabling them to be Autowired
@Import(MvcConfig.class)
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class SettlementRestControllerTest extends ASpringTest {

  @Autowired
  private ITranslator<TradeAgreementMessage, TradeAgreement> agreementTranslator;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private SettlementService mockSettlementService;

  private JacksonTester<List<Long>> missionIdJsonTester;

  private static final String APPLICATION_X_PROTOBUF = "application/x-protobuf";

  private static final Long MISSION_ID_1 = 1091L;

  @Before
  public void setup() {
    ObjectMapper objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, objectMapper);
  }

  @Test
  @FileParameters(value = "src/test/resources/testSettleAgreement.data",
      mapper = JsonTestFileMapper.class)
  public void testSettleAgreement_JSON(final String agreementJson,
      final String expectedMissionIdJson)
      throws Exception {

    log.info("Agreement json to post {}", agreementJson);

    List<Long> expectedMissionIds = missionIdJsonTester.parse(expectedMissionIdJson).getObject();

    List<String> expectedResponseJsonList = expectedMissionIds.stream()
        .map(id -> "/settlementEngine/mission/" + id).collect(Collectors.toList());

    log.info("Expected json response {}", expectedResponseJsonList);

    TradeAgreementMessage message = jsonToMessage(agreementJson);
    log.info("Agreement objects that the service will expect {}", message);

    given(this.mockSettlementService.spawnMissions(singletonList(transformAgreement(message))))
        .willReturn(Sets.newTreeSet(expectedMissionIds));

    ResultActions resultActions = this.mvc
        .perform(post("/settlementEngine?requestId=1234")
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(agreementJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", containsInAnyOrder(expectedResponseJsonList.toArray())));

    verifyAuditHeaders(resultActions);
  }

  private TradeAgreementMessage jsonToMessage(String json) throws IOException {
    byte[] bytes = json.getBytes();
    ByteInputStream bis = new ByteInputStream(json.getBytes(), bytes.length);
    TradeAgreementMessage.Builder builder = TradeAgreementMessage.newBuilder();
    new JsonFormat().merge(bis, builder);
    return builder.build();
  }

  @Test
  public void testSettleAgreement_Protobuf() throws Exception {

    TradeAgreementMessage message = TradeAgreementMessage.newBuilder()
        .setInstrument("IBM").setInternalParty("INT-1")
        .setExternalParty("EXT-1").setBuySell("B").setQty(100).build();

    given(this.mockSettlementService.spawnMissions(singletonList(transformAgreement(message))))
        .willReturn(Sets.newTreeSet(singletonList(1L)));

    ResultActions resultActions = this.mvc.perform(post("/settlementEngine?requestId=1234")
        .contentType(APPLICATION_X_PROTOBUF)
        .content(message.toByteArray()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", containsInAnyOrder("/settlementEngine/mission/1")));

    verifyAuditHeaders(resultActions);
  }

  private TradeAgreement transformAgreement(TradeAgreementMessage message) {
    return agreementTranslator.translate(message);
  }

  @Test
  public void testGetMissionFound_JSON() throws Exception {
    String depot = "DTC";
    String externapParty = "EXT-1";
    String instrument = "IBM";
    String direction = "REC";
    double qty = 100;

    SettlementMission testMission = SettlementMission.builder().id(MISSION_ID_1).depot(depot)
        .externalParty(externapParty).instrument(instrument).direction(direction).qty(qty).build();
    log.info("Test mission: {}", testMission);

    given(this.mockSettlementService.findMission(MISSION_ID_1))
        .willReturn(Optional.of(testMission));

    ResultActions resultActions =
        this.mvc.perform(get("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
            .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(MISSION_ID_1.intValue())))
            .andExpect(jsonPath("$.external_party", is(externapParty)))
            .andExpect(jsonPath("$.instrument", is(instrument)))
            .andExpect(jsonPath("$.direction", is(direction)))
            .andExpect(jsonPath("$.qty", is(qty)));

    verifyAuditHeaders(resultActions);
  }

  @Test
  public void testGetMissionFound_Protobuf() throws Exception {
    String depot = "DTC";
    String externapParty = "EXT-1";
    String instrument = "IBM";
    String direction = "REC";
    double qty = 100;

    SettlementMission testMission = SettlementMission.builder().id(MISSION_ID_1).depot(depot)
        .externalParty(externapParty).instrument(instrument).direction(direction).qty(qty).build();
    log.info("Test mission: {}", testMission);

    given(this.mockSettlementService.findMission(MISSION_ID_1))
        .willReturn(Optional.of(testMission));

    ResultActions resultActions = this.mvc.perform(
        get("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
            .accept(APPLICATION_X_PROTOBUF))
        .andExpect(status().isOk());

    verifyAuditHeaders(resultActions);
  }

  @Test
  public void testGetMissionFound_XML() throws Exception {
    String depot = "DTC";
    String externalParty = "EXT-1";
    String instrument = "IBM";
    String direction = "REC";
    double qty = 100;

    SettlementMission testMission = SettlementMission.builder().id(MISSION_ID_1).depot(depot)
        .externalParty(externalParty).instrument(instrument).direction(direction).qty(qty).build();
    log.info("Test mission: {}", testMission);

    given(this.mockSettlementService.findMission(MISSION_ID_1))
        .willReturn(Optional.of(testMission));

    ResultActions resultActions =
        this.mvc.perform(get("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
            .accept(MediaType.APPLICATION_XML)).andExpect(status().isOk())
            .andExpect(xpath("//id").string(MISSION_ID_1.toString()))
            .andExpect(xpath("//external_party").string(externalParty))
            .andExpect(xpath("//instrument").string(instrument))
            .andExpect(xpath("//direction").string(direction))
            .andExpect(xpath("//qty").string(String.valueOf(qty)));

    verifyAuditHeaders(resultActions);
  }

  @Test
  public void testGetMissionNotFound() throws Exception {
    long msnId = 1091L;

    given(this.mockSettlementService.findMission(msnId)).willReturn(Optional.empty());

    ResultActions resultActions = this.mvc
        .perform(get("/settlementEngine/mission/" + msnId + "?requestId=1234")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());
    verifyAuditHeaders(resultActions);
  }

  @Test
  public void testIncorrectlyFormattedAgreement() throws Exception {
    String expectedMessage = "Incorrectly formatted message.  Please consult the documentation.";

    this.mvc.perform(post("/settlementEngine?requestId=1234")
        .contentType(APPLICATION_X_PROTOBUF).content("invalidAgreementBytes"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name())))
        .andExpect(jsonPath("$.message", is(expectedMessage)));
  }

  @Test
  public void testDataAccessFailure() throws Exception {
    DataAccessException exception = new DataAccessException("msg") {
    };
    when(mockSettlementService.findMission(MISSION_ID_1)).thenThrow(exception);

    this.mvc.perform(get("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
        .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.name())))
        .andExpect(jsonPath("$.message", is("An internal application error occurred.")));
  }

  /**
   * Verifies required audit fields are present
   *
   * @param resultActions The resultActions object wrapping the response
   * @throws Exception On any validation exception
   */
  private void verifyAuditHeaders(ResultActions resultActions) throws Exception {
    resultActions.andExpect(header().string("requestReceivedTime", not(isEmptyOrNullString())));
    resultActions
        .andExpect(header().string("requestElapsedTimeMillis", not(isEmptyOrNullString())));
    resultActions.andExpect(header().string("externalQueryId", not(isEmptyOrNullString())));
    resultActions.andExpect(header().string("internalQueryId", not(isEmptyOrNullString())));
  }

}
