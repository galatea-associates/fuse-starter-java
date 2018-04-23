package org.galatea.starter.entrypoint;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Sets;
import junitparams.JUnitParamsRunner;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.ProtoMessageTranslationConfig;
import org.galatea.starter.TestConfig;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementResponseProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessages;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.ObjectSupplier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode(callSuper = true)
// We don't load the entire spring application context for this test.
@WebMvcTest(SettlementRestProtoController.class)
// Import Beans from Configuration, enabling them to be Autowired
@Import({ProtoMessageTranslationConfig.class, TestConfig.class})
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class SettlementProtoRestControllerTest extends ASpringTest {

  private static final String APPLICATION_X_PROTOBUF = "application/x-protobuf";

  @Autowired
  private ObjectSupplier<SettlementMission> settlementMissionSupplier;

  @Autowired
  private ObjectSupplier<TradeAgreement> tradeAgreementSupplier;

  @Autowired
  private ObjectSupplier<TradeAgreementProtoMessage> tradeAgreementProtoSupplier;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private SettlementService mockSettlementService;

  @Test
  public void testSettleAgreement() throws Exception {
    TradeAgreement agreement = tradeAgreementSupplier.get();
    TradeAgreementProtoMessage message = tradeAgreementProtoSupplier.get();
    Long expectedId = 1L;

    log.info("Agreement to post {}. Proto message {}. Expected id {}", agreement, message,
        expectedId);

    TradeAgreementProtoMessages messages = TradeAgreementProtoMessages.newBuilder()
        .addMessage(message).build();

    given(this.mockSettlementService.spawnMissions(singletonList(agreement)))
        .willReturn(Sets.newTreeSet(singletonList(expectedId)));

    MvcResult result = this.mvc.perform(
        post("/settlementEngineProto?requestId=1234").contentType(APPLICATION_X_PROTOBUF)
            .accept(APPLICATION_X_PROTOBUF).content(messages.toByteArray()))
        .andExpect(status().isOk()).andReturn();

    SettlementResponseProtoMessage received = SettlementResponseProtoMessage.parseFrom(result.getResponse().getContentAsByteArray());
    assertTrue(!received.getSpawnedMissionPathsList().isEmpty());
    assertTrue(received.getSpawnedMissionPathsList().contains("/settlementEngineProto/mission/" + expectedId));
  }
}
