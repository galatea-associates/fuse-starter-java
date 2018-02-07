package org.galatea.starter.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.domain.TradeAgreement;
import org.joda.money.BigMoney;

import java.io.IOException;

@Slf4j
public class TradeAgreementDeserializer extends FuseDeserializer {

  public TradeAgreementDeserializer() {
    super(TradeAgreement.class);
  }

  @Override
  public TradeAgreement deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode node = jsonParser.readValueAsTree();

    return TradeAgreement.builder()
        .instrument(getIfValid(node, "instrument", JsonNodeType.STRING,
            n -> n.get("instrument").asText()))
        .internalParty(getIfValid(node, "internalParty", JsonNodeType.STRING,
            n -> n.get("internalParty").asText()))
        .externalParty(getIfValid(node, "externalParty", JsonNodeType.STRING,
            n -> n.get("externalParty").asText()))
        .buySell(getIfValid(node, "buySell", JsonNodeType.STRING,
            n -> n.get("buySell").asText()))
        .qty(getIfValid(node, "qty", JsonNodeType.NUMBER,
            n -> n.get("qty").asDouble()))
        .proceeds(getIfValid(node, "proceeds", JsonNodeType.STRING,
            this::getProceeds))
        .build();
  }

  protected BigMoney getProceeds(JsonNode node) {
    try {
      return BigMoney.parse(node.get("proceeds").asText());
    } catch (IllegalArgumentException e) {
      log.error("Could not parse proceeds from request.", e);
      return null;
    }
  }
}
