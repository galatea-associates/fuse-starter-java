
package org.galatea.starter.entrypoint;

import static org.galatea.starter.Utilities.getTradeAgreement;
import static org.galatea.starter.Utilities.getTradeAgreementJsonFromFile;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import feign.jackson.JacksonDecoder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.FXRateResponse;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.service.IAgreementTransformer;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.deserializers.FXRateResponseDeserializer;
import org.galatea.starter.utils.deserializers.TradeAgreementDeserializer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jms.core.JmsTemplate;

import java.io.IOException;
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
      String agreementJson = getTradeAgreementJsonFromFile("Correct_IBM_Agreement.json");
      log.warn("Agreement json to put on queue {}", agreementJson);

    List<TradeAgreement> agreements = Arrays.asList(getTradeAgreement());
    log.warn("Agreement objects that the service will expect {}", agreements);

    jmsTemplate.send(queueName, s -> {
        TextMessage msg = s.createTextMessage(agreementJson);
        return msg;
    });

      verify(mockSettlementService, timeout(10000)).spawnMissions(agreements);

    }

}
