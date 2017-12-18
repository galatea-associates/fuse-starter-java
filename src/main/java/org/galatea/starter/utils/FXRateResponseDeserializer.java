package org.galatea.starter.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.FXRateException;
import org.galatea.starter.domain.FXRateResponse;
import org.joda.money.CurrencyUnit;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Slf4j
public class FXRateResponseDeserializer extends StdDeserializer {

    public FXRateResponseDeserializer(Class<?>vc) {
        super(vc);
    }

    @Override
    public FXRateResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws FXRateException {
        JsonNode node;
        try {
            node = JsonChecker.getNode(jsonParser, getFieldInfo());
        } catch (IOException e) {
            log.error(e.toString());
            throw new FXRateException(e.getMessage());
        }

        Date date = getDate(node);

        return FXRateResponse.builder()
                .baseCurrency(CurrencyUnit.of(node.get("base").asText()))
                .validOn(date)
                .exchangeRate(BigDecimal.valueOf(node.get("USD").asDouble()))
                .build();
    }

    private HashMap<String, JsonNodeType> getFieldInfo() {
        HashMap<String, JsonNodeType> fieldInfo = new HashMap<>();
        fieldInfo.put("date", JsonNodeType.STRING);
        fieldInfo.put("base", JsonNodeType.STRING);
        fieldInfo.put("USD", JsonNodeType.NUMBER);
        return fieldInfo;
    }

    private Date getDate(JsonNode node) throws FXRateException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(node.get("date").asText());
        } catch (ParseException e) {
            log.error(e.toString());
            throw new FXRateException("Could not parse date from FXRate API.");
        }
    }
}
