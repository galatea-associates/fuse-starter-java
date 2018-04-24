package org.galatea.starter;

import com.googlecode.protobuf.format.JsonFormat;

import org.galatea.starter.entrypoint.messagecontracts.Messages;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MessageUtil {

  public static Messages.TradeAgreementMessages jsonToTradeAgreementMessages(String json)
      throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes());
    Messages.TradeAgreementMessages.Builder builder = Messages.TradeAgreementMessages.newBuilder();
    new JsonFormat().merge(bis, builder);
    return builder.build();
  }

}
