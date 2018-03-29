package org.galatea.starter;

import com.googlecode.protobuf.format.JsonFormat;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import org.galatea.starter.entrypoint.messagecontracts.Messages;

import java.io.IOException;

public class MessageUtil {

  public static Messages.TradeAgreementMessage jsonToTradeAgreement(String json)
      throws IOException {
    byte[] bytes = json.getBytes();
    ByteInputStream bis = new ByteInputStream(json.getBytes(), bytes.length);
    Messages.TradeAgreementMessage.Builder builder = Messages.TradeAgreementMessage.newBuilder();
    new JsonFormat().merge(bis, builder);
    return builder.build();
  }

}
