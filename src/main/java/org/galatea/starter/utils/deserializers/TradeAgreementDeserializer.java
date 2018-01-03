package org.galatea.starter.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.TradeAgreement;
import org.joda.money.BigMoney;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class TradeAgreementDeserializer extends FuseDeserializer {

  protected static final HashMap<String, JsonNodeType> fieldMap = getFieldMap();

  public TradeAgreementDeserializer() {
    super(TradeAgreement.class);
  }

  @Override
  public TradeAgreement deserialize(JsonParser jsonParser,
      DeserializationContext deserializationContext) throws IOException {
    JsonNode node = getAndCheckRootNode(jsonParser, fieldMap);

    return TradeAgreement.builder()
        .instrument(node.get("instrument").asText())
        .internalParty(node.get("internalParty").asText())
        .externalParty(node.get("externalParty").asText())
        .buySell(node.get("buySell").asText())
        .qty(node.get("qty").asDouble())
        .proceeds(getProceeds(node))
        .build();
  }

  protected static HashMap<String, JsonNodeType> getFieldMap() {
    HashMap<String, JsonNodeType> fieldInfo = new HashMap<>();
    fieldInfo.put("instrument", JsonNodeType.STRING);
    fieldInfo.put("internalParty", JsonNodeType.STRING);
    fieldInfo.put("externalParty", JsonNodeType.STRING);
    fieldInfo.put("buySell", JsonNodeType.STRING);
    fieldInfo.put("qty", JsonNodeType.NUMBER);
    fieldInfo.put("proceeds", JsonNodeType.STRING);
    return fieldInfo;
  }

  // See Effective Java 2nd Ed. Item 61
  protected BigMoney getProceeds(JsonNode node) throws IOException {
    try {
      return BigMoney.parse(node.get("proceeds").asText());
    } catch (IllegalArgumentException e) {
      log.error("Could not parse proceeds from request.", e);
      throw new IOException("Could not parse proceeds from request.", e);
    }
  }
}
