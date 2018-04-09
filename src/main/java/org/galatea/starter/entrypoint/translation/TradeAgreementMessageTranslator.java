package org.galatea.starter.entrypoint.translation;

import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.Messages;
import org.galatea.starter.utils.translation.ITranslator;

public class TradeAgreementMessageTranslator implements
    ITranslator<Messages.TradeAgreementMessage, TradeAgreement> {

  @Override
  public TradeAgreement translate(Messages.TradeAgreementMessage message) {
    return TradeAgreement.builder()
        .id(message.getId())
        .buySell(message.getBuySell())
        .externalParty(message.getExternalParty())
        .instrument(message.getInstrument())
        .internalParty(message.getInternalParty())
        .qty(message.getQty()).build();
  }

}
