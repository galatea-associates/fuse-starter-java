
package org.galatea.starter.entrypoint;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.TextMessage;


@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
@RunWith(SpringRunner.class)
@SpringBootTest
public class SettlementJmsListenerTest {

  @Autowired
  protected JmsTemplate jmsTemplate;

  @Test
  public void test() throws InterruptedException {
    // jmsTemplate.convertAndSend("sandbox.agreement",
    // "[{" + "\"instrument\": \"IBM\"," + "\"_type\": \"TradeAgreement\","
    // + "\"internalParty\": \"INT-1\"," + "\"externalParty\": \"EXT-1\","
    // + "\"buySell\": \"B\"," + "\"qty\": 100" + "}]",
    // m -> {
    // m.setStringProperty("_type", "org.galatea.starter.domain.TradeAgreement");
    // return m;
    // });

    jmsTemplate.send("sandbox.agreement", s -> {
      TextMessage msg = s.createTextMessage("{" + "\"instrument\": \"IBM\","
          + "\"_type\": \"TradeAgreement\"," + "\"internalParty\": \"INT-1\","
          + "\"externalParty\": \"EXT-1\"," + "\"buySell\": \"B\"," + "\"qty\": 100" + "}");
      // msg.setStringProperty("_type", "org.galatea.starter.domain.TradeAgreement");
      return msg;
    });


    Thread.sleep(10 * 1000);

  }

}
