package org.galatea.starter.entrypoint;

import static org.junit.Assert.assertEquals;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessage;
import org.galatea.starter.testutils.TestDataGenerator;
import org.galatea.starter.utils.translation.ITranslator;
import org.galatea.starter.utils.translation.TranslationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests the implementation of ITranslator&lt;byte[], TradeAgreement&gt;
 */
@Slf4j
@ToString
@EqualsAndHashCode(callSuper = true)
@SpringBootTest
public class TradeAgreementTranslatorTest extends ASpringTest {

  @Autowired
  protected ITranslator<byte[], TradeAgreement> translator;

  @Test
  public void translateGoodMessage() {
    TradeAgreementProtoMessage message
        = TestDataGenerator.defaultTradeAgreementProtoMessageData().build();
    TradeAgreement agreement = TestDataGenerator.defaultTradeAgreementData().build();

    TradeAgreement result = translator.translate(message.toByteArray());
    assertEquals("The object produced by the translator did not match what was expected.",
        agreement, result);
  }

  @Test(expected = TranslationException.class)
  public void translateBadMessage() {
    byte[] nullBuffer = new byte[]{1, 2, 3, 4, 5, 6};
    translator.translate(nullBuffer);
  }
}
