package org.galatea.starter.entrypoint;


import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import java.io.StringWriter;
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
import org.galatea.starter.TestConfig;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessages;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.ObjectSupplier;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RequiredArgsConstructor
@Slf4j
// We don't load the entire spring application context for this test.
@WebMvcTest(SettlementRestController.class)
// Import Beans from Configuration, enabling them to be Autowired
@Import({MessageTranslationConfig.class, TestConfig.class})
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class SettlementRestControllerTest extends ASpringTest {

  @Autowired
  private ObjectSupplier<SettlementMission> settlementMissionSupplier;

  @Autowired
  ITranslator<TradeAgreementMessages, List<TradeAgreement>> tradeAgreementTranslator;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private SettlementService mockSettlementService;

  private JacksonTester<TradeAgreementMessages> agreementJsonTester;

  private JacksonTester<List<Long>> missionIdJsonTester;

  private ObjectMapper objectMapper;


  private static final Long MISSION_ID_1 = 1091L;

  @Before
  public void setup() {
    objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, objectMapper);
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

    ResultActions resultActions = this.mvc
        .perform(post("/settlementEngine?requestId=1234")
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(agreementJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.spawnedMissions",
            containsInAnyOrder(expectedResponseJsonList.toArray())));

    verifyAuditHeaders(resultActions);
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

    ResultActions resultActions = this.mvc.perform(post("/settlementEngine?requestId=1234")
        .contentType(MediaType.APPLICATION_XML)
        .accept(MediaType.APPLICATION_XML)
        .content(xml))
        .andExpect(status().isOk());

    verifyAuditHeaders(resultActions);
  }

  private List<TradeAgreement> toTradeAgreements(TradeAgreementMessages messages) {
    return tradeAgreementTranslator.translate(messages);
  }

  @Test
  public void testGetMissionFound_JSON() throws Exception {
    SettlementMission mission = settlementMissionSupplier.get();
    log.info("Test mission: {}", mission);

    given(this.mockSettlementService.findMission(MISSION_ID_1))
        .willReturn(Optional.of(mission));

    ResultActions resultActions =
        this.mvc.perform(get("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
            .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(mission.getId().intValue())))
            .andExpect(jsonPath("$.externalParty", is(mission.getExternalParty())))
            .andExpect(jsonPath("$.instrument", is(mission.getInstrument())))
            .andExpect(jsonPath("$.direction", is(mission.getDirection())))
            .andExpect(jsonPath("$.qty", is(mission.getQty())));

    verifyAuditHeaders(resultActions);
  }



  @Test
  public void testGetMissionFound_XML() throws Exception {
    SettlementMission mission = settlementMissionSupplier.get();
    log.info("Test mission: {}", mission);

    given(this.mockSettlementService.findMission(MISSION_ID_1))
        .willReturn(Optional.of(mission));

    ResultActions resultActions =
        this.mvc.perform(get("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
            .accept(MediaType.APPLICATION_XML)).andExpect(status().isOk())
            .andExpect(xpath("//id").string(mission.getId().toString()))
            .andExpect(xpath("//externalParty").string(mission.getExternalParty()))
            .andExpect(xpath("//instrument").string(mission.getInstrument()))
            .andExpect(xpath("//direction").string(mission.getDirection()))
            .andExpect(xpath("//qty").string(String.valueOf(mission.getQty())));

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
        .contentType(MediaType.APPLICATION_JSON).content("invalidAgreementBytes"))
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

  @Test
  public void testUpdateMission() throws Exception {
    TradeAgreement expectedAgreement = TradeAgreement.builder().instrument("IBM")
        .internalParty("INT-1").externalParty("EXT-1").buySell("B").qty(100d).build();
    SettlementMission settlementMission = settlementMissionSupplier.get();

    when(mockSettlementService.missionExists(MISSION_ID_1))
        .thenReturn(true);
    when(mockSettlementService.updateMission(MISSION_ID_1, expectedAgreement))
        .thenReturn(Optional.of(settlementMission));

    this.mvc.perform(put("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
        .content(objectMapper.convertValue(expectedAgreement, JsonNode.class).toString())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }

  @Test
  public void testUpdateNonExistentMission() throws Exception {
    TradeAgreement expectedAgreement = TradeAgreement.builder().instrument("IBM")
        .internalParty("INT-1").externalParty("EXT-1").buySell("B").qty(100d).build();

    when(mockSettlementService.missionExists(MISSION_ID_1))
        .thenReturn(false);

    this.mvc.perform(put("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
        .content(objectMapper.convertValue(expectedAgreement, JsonNode.class).toString())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testDeleteMission() throws Exception {
    doNothing().when(mockSettlementService).deleteMission(MISSION_ID_1);

    this.mvc.perform(delete("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
        .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }

  @Test
  public void testDeleteFakeMission() throws Exception {
    doThrow(EmptyResultDataAccessException.class).when(mockSettlementService)
        .deleteMission(MISSION_ID_1);

    this.mvc.perform(delete("/settlementEngine/mission/" + MISSION_ID_1 + "?requestId=1234")
        .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());
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
