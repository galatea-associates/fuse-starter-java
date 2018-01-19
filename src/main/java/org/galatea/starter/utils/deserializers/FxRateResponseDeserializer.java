package org.galatea.starter.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.domain.FxRateResponse;
import org.joda.money.CurrencyUnit;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
public class FxRateResponseDeserializer extends FuseDeserializer {

  public FxRateResponseDeserializer() {
    super(FxRateResponse.class);
  }

  @Override
  public FxRateResponse deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode rootNode = jsonParser.readValueAsTree();
    JsonNode ratesNode = rootNode.path("rates");

    return FxRateResponse.builder()
        .baseCurrency(getIfValid(rootNode, "base", JsonNodeType.STRING,
            n -> CurrencyUnit.of(n.get("base").asText())))
        .validOn(getIfValid(rootNode, "date", JsonNodeType.STRING,
            this::getDate))
        .exchangeRate(getIfValid(ratesNode, "USD", JsonNodeType.NUMBER,
            n -> BigDecimal.valueOf(n.get("USD").asDouble())))
        .build();
  }

  protected LocalDate getDate(JsonNode node) {
    try {
      return LocalDate.parse(node.get("date").asText());
    } catch (DateTimeParseException e) {
      log.error("Unable to parse date from FX Rate API.", e);
      return null;
    }
  }
}
