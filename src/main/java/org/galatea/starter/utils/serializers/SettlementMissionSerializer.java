package org.galatea.starter.utils.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.galatea.starter.domain.SettlementMission;
import org.joda.money.BigMoney;

import java.io.IOException;

public class SettlementMissionSerializer extends StdSerializer<SettlementMission> {

  protected SettlementMissionSerializer() {
    super(SettlementMission.class);
  }

  @Override
  public void serialize(SettlementMission mission, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {

    jsonGenerator.writeStartObject();
    jsonGenerator.writeNumberField("id", mission.getId());
    jsonGenerator.writeStringField("instrument", mission.getInstrument());
    jsonGenerator.writeStringField("externalParty", mission.getExternalParty());
    jsonGenerator.writeStringField("depot", mission.getDepot());
    jsonGenerator.writeStringField("direction", mission.getDirection());
    jsonGenerator.writeNumberField("qty", mission.getQty());
    jsonGenerator.writeStringField("proceeds", writeBigMoney(mission.getProceeds()));
    jsonGenerator.writeStringField("usdProceeds", writeBigMoney(mission.getUsdProceeds()));
    jsonGenerator.writeEndObject();
  }

  protected String writeBigMoney(BigMoney money) {
    return money.getCurrencyUnit().toString() + " " + money.getAmount().toString();
  }
}
