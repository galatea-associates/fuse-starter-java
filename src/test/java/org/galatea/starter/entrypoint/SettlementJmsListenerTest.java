package org.galatea.starter.entrypoint;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.jms.TextMessage;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessage;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.testutils.TestDataGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;


@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode(callSuper = true)
@SpringBootTest
public class SettlementJmsListenerTest extends ASpringTest {

  @Autowired
  protected JmsTemplate jmsTemplate;

  @MockBean
  private SettlementService mockSettlementService;

  @Value("${jms.agreement-queue-json}")
  protected String jsonQueueName;

  @Value("${jms.agreement-queue-proto}")
  protected String protoQueueName;

  /*
  The ActiveMQ broker doesn't get shutdown after each test so we have the cleanup method in the base
  class to kill it manually. The side effect of that method is that it kills the listener containers
  so the next test that runs in the same class will not have any active listeners in the queue.

  By annotating tests with @DirtiesContext we force the spring context used by one test to be discarded
  and a new context to be supplied for the following test.
   */

  @Test
  @DirtiesContext
  public void testSettleOneAgreementJson() throws IOException {
    String message = readData("Test_IBM_Agreement.json").replace("\n", "")
        .replace("[", "").replace("]", "");

    TradeAgreement agreement = TradeAgreement.builder().instrument("IBM").internalParty("INT-1")
        .externalParty("EXT-1").buySell("B").qty(100d).build();
    List<TradeAgreement> expectedAgreements = Collections.singletonList(agreement);

    log.info("Agreement JSON to put in the queue: {}", message);
    log.info("Agreement objects the service will expect {}", expectedAgreements);

    jmsTemplate.send(jsonQueueName, s -> {
      TextMessage msg = s.createTextMessage(message);
      return msg;
    });

    verify(mockSettlementService, timeout(10000)).spawnMissions(expectedAgreements);
  }

  @Test
  @DirtiesContext
  public void testSettleOneAgreementProto() {
    TradeAgreementProtoMessage message
        = TestDataGenerator.defaultTradeAgreementProtoMessageData().build();
    TradeAgreement agreement = TestDataGenerator.defaultTradeAgreementData().build();

    log.info("Agreement message to put on queue {}", message);
    List<TradeAgreement> agreements = Collections.singletonList(agreement);
    log.info("Agreement objects that the service will expect {}", agreements);

    jmsTemplate.convertAndSend(protoQueueName, message.toByteArray());

    verify(mockSettlementService, timeout(10000)).spawnMissions(agreements);
  }
}
