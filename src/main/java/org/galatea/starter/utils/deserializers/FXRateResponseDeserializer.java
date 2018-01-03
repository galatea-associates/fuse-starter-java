package org.galatea.starter.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.FXRateResponse;
import org.joda.money.CurrencyUnit;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

@Slf4j
public class FXRateResponseDeserializer extends FuseDeserializer {

  protected static final HashMap<String, JsonNodeType> rootFieldMap = getRootFieldMap();
  protected static final HashMap<String, JsonNodeType> ratesFieldMap = getRatesFieldMap();

  public FXRateResponseDeserializer() {
    super(FXRateResponse.class);
  }

  @Override
  public FXRateResponse deserialize(JsonParser jsonParser,
      DeserializationContext deserializationContext) throws IOException {
    JsonNode rootNode = getAndCheckRootNode(jsonParser, rootFieldMap);
    JsonNode ratesNode = checkNode(rootNode.path("rates"), ratesFieldMap);

    return FXRateResponse.builder()
        .baseCurrency(CurrencyUnit.of(rootNode.get("base").asText()))
        .validOn(getDate(rootNode))
        .exchangeRate(BigDecimal.valueOf(ratesNode.get("USD").asDouble()))
        .build();
  }

  protected static HashMap<String, JsonNodeType> getRootFieldMap() {
    HashMap<String, JsonNodeType> fieldInfo = new HashMap<>();
    fieldInfo.put("date", JsonNodeType.STRING);
    fieldInfo.put("base", JsonNodeType.STRING);
    fieldInfo.put("rates", JsonNodeType.OBJECT);
    return fieldInfo;
  }

  protected static HashMap<String, JsonNodeType> getRatesFieldMap() {
    HashMap<String, JsonNodeType> fieldInfo = new HashMap<>();
    fieldInfo.put("USD", JsonNodeType.NUMBER);
    return fieldInfo;
  }

  // See Effective Java 2nd Ed. Item 61
  protected LocalDate getDate(JsonNode node) throws IOException {
    try {
      return LocalDate.parse(node.get("date").asText());
    } catch (DateTimeParseException e) {
      log.error("Unable to parse date from FX Rate API.", e);
      throw new IOException("Unable to parse date from FX Rate API.", e);
    }
  }
}
