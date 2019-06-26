package org.galatea.starter.entrypoint;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Sets;
import java.util.Optional;
import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.ProtoMessageTranslationConfig;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementMissionProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementResponseProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessages;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.testutils.TestDataGenerator;
import org.galatea.starter.utils.translation.ITranslator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Tests for the protobuf controller. Right now this doesn't test the exception scenarios as there
 * is a problem serializing ResponseBody<> to protobuf. I need to investigate this a bit further.
 */
@RequiredArgsConstructor
@Slf4j
// We don't load the entire spring application context for this test.
@WebMvcTest(SettlementProtoRestController.class)
// Import Beans from Configuration, enabling them to be Autowired
@Import({ProtoMessageTranslationConfig.class, RestExceptionHandler.class})
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class SettlementProtoRestControllerTest extends ASpringTest {

  private static final String APPLICATION_X_PROTOBUF = "application/x-protobuf";

  @Autowired
  private ITranslator<SettlementMission, SettlementMissionProtoMessage> settlementMissionTranslator;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private SettlementService mockSettlementService;

  private static final Long MISSION_ID_1 = 100L;

  @Test
  public void testSettleAgreement() throws Exception {
    TradeAgreement agreement = TestDataGenerator.defaultTradeAgreementData().build();
    TradeAgreementProtoMessage message
        = TestDataGenerator.defaultTradeAgreementProtoMessageData().build();
    Long expectedId = 1L;

    log.info("Agreement to post {}. Proto message {}. Expected id {}", agreement, message,
        expectedId);

    TradeAgreementProtoMessages messages = TradeAgreementProtoMessages.newBuilder()
        .addMessage(message).build();

    given(this.mockSettlementService.spawnMissions(singletonList(agreement)))
        .willReturn(Sets.newTreeSet(singletonList(expectedId)));

    MvcResult result = this.mvc.perform(
        post("/settlementEngine?requestId=1234").contentType(APPLICATION_X_PROTOBUF)
            .accept(APPLICATION_X_PROTOBUF).content(messages.toByteArray()))
        .andExpect(status().isOk()).andReturn();

    SettlementResponseProtoMessage received = SettlementResponseProtoMessage
        .parseFrom(result.getResponse().getContentAsByteArray());
    assertTrue(!received.getSpawnedMissionPathsList().isEmpty());
    assertTrue(received.getSpawnedMissionPathsList()
        .contains("/settlementEngine/mission/" + expectedId));
  }

  @Test
  public void testGetMission() throws Exception {
    SettlementMission mission = TestDataGenerator.defaultSettlementMissionData().build();
    SettlementMissionProtoMessage expectedMessage = settlementMissionTranslator.translate(mission);

    log.info("Test mission: {}", mission);

    given(this.mockSettlementService.findMission(MISSION_ID_1)).willReturn(Optional.of(mission));

    MvcResult result = this.mvc.perform(
        get("/settlementEngine/mission/" + MISSION_ID_1 + "?requesId=1234")
            .accept(APPLICATION_X_PROTOBUF)).andExpect(status().isOk()).andReturn();

    SettlementMissionProtoMessage message = SettlementMissionProtoMessage
        .parseFrom(result.getResponse().getContentAsByteArray());
    assertEquals(expectedMessage, message);
  }

  @Test
  public void testGetMissionNotFound() throws Exception {
    given(this.mockSettlementService.findMission(MISSION_ID_1)).willReturn(Optional.empty());

    this.mvc.perform(
        get("/settlementEngine/mission/" + MISSION_ID_1 + "?requesId=1234")
            .accept(APPLICATION_X_PROTOBUF))
        .andExpect(status().is4xxClientError());
  }
}
