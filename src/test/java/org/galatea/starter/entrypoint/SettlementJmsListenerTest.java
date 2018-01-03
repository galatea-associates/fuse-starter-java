
package org.galatea.starter.entrypoint;

import static org.galatea.starter.Utilities.getTradeAgreement;
import static org.galatea.starter.Utilities.getJsonFromFile;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.service.IAgreementTransformer;
import org.galatea.starter.service.SettlementService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;

import java.util.Arrays;
import java.util.List;

import javax.jms.TextMessage;


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

  @MockBean
  private IAgreementTransformer agreementTransformer;

  @Value("${jms.agreement-queue}")
  protected String queueName;

  @Before
  public void setup() {
    ObjectMapper objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, objectMapper);
  }

  @Test
  public void testSettleOneAgreement() throws Exception {

    // Read the json file but get rid of the array bookends since the jms entry point doesn't support that
    String agreementJson = getJsonFromFile("TradeAgreement/Correct_IBM_Agreement.json");
    log.info("Agreement json to put on queue {}", agreementJson);

    List<TradeAgreement> agreements = Arrays.asList(getTradeAgreement());
    log.info("Agreement objects that the service will expect {}", agreements);

    jmsTemplate.send(queueName, s -> {
      TextMessage msg = s.createTextMessage(agreementJson);
      return msg;
    });

    verify(mockSettlementService, timeout(10000)).spawnMissions(agreements);

  }

}
