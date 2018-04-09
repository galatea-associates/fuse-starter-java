package org.galatea.starter.entrypoint.translation;

import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.Messages;
import org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessage;
import org.galatea.starter.utils.translation.ITranslator;

import java.util.List;
import java.util.stream.Collectors;

public class TradeAgreementMessagesTranslator implements
    ITranslator<Messages.TradeAgreementMessages, List<TradeAgreement>> {

  private ITranslator<TradeAgreementMessage, TradeAgreement> translator;

  public TradeAgreementMessagesTranslator(
      ITranslator<TradeAgreementMessage, TradeAgreement> translator) {
    this.translator = translator;
  }

  @Override
  public List<TradeAgreement> translate(Messages.TradeAgreementMessages messages) {
    return messages.getMessageList().stream()
        .map(message -> translator.translate(message))
        .collect(Collectors.toList());
  }

}
