package org.galatea.starter.utils.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;

import org.galatea.starter.domain.SettlementMission;

import java.io.IOException;

@Slf4j
@Log(enterLevel = Log.Level.INFO, exitLevel = Log.Level.INFO)
public class SettlementMissionSerializer extends StdSerializer<SettlementMission> {

  public SettlementMissionSerializer() {
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
    jsonGenerator.writeStringField("proceeds", mission.getProceeds().getCurrencyUnit().toString() + " " + mission.getProceeds().getAmount().toString());
    jsonGenerator.writeStringField("usdProceeds", mission.getUsdProceeds().getCurrencyUnit().toString() + " " + mission.getUsdProceeds().getAmount().toString());
    jsonGenerator.writeEndObject();
  }
}
