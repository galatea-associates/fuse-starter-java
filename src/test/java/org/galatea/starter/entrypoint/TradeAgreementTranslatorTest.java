package org.galatea.starter.entrypoint;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessage;
import org.galatea.starter.utils.ObjectSupplier;
import org.galatea.starter.utils.translation.ITranslator;
import org.galatea.starter.utils.translation.TranslationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.Assert.assertEquals;

/**
 * Tests the implementation of ITranslator&lt;byte[], TradeAgreement&gt;
 */
@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode(callSuper = true)
@SpringBootTest
public class TradeAgreementTranslatorTest extends ASpringTest {
  @Autowired
  protected ObjectSupplier<TradeAgreementMessage> messageSupplier;

  @Autowired
  protected ObjectSupplier<TradeAgreement> agreementSupplier;

  @Autowired
  protected ITranslator<byte[], TradeAgreement> translator;

  @Test
  public void translateGoodMessage() {
    TradeAgreementMessage message = messageSupplier.get();
    TradeAgreement agreement = agreementSupplier.get();

    TradeAgreement result = translator.translate(message.toByteArray());
    assertEquals("The object produced by the translator did not match what was expected.", agreement, result);
  }

  @Test(expected = TranslationException.class)
  public void translateBadMessage() {
    byte[] nullBuffer = new byte[]{1, 2, 3, 4, 5, 6};
    translator.translate(nullBuffer);
  }
}
