package org.galatea.starter.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
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
public class FXRateResponseDeserializer extends FuseDeserializer {

    protected static final HashMap<String, JsonNodeType> fieldMap = getFieldMap();
    protected static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public FXRateResponseDeserializer() {
        super(FXRateResponse.class);
    }

    @Override
    public FXRateResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws FXRateException {
        JsonNode node = getNode(jsonParser);
        Date date = getDate(node);

        return FXRateResponse.builder()
                .baseCurrency(CurrencyUnit.of(node.get("base").asText()))
                .validOn(date)
                .exchangeRate(BigDecimal.valueOf(node.get("USD").asDouble()))
                .build();
    }

    protected static HashMap<String, JsonNodeType> getFieldMap() {
        HashMap<String, JsonNodeType> fieldInfo = new HashMap<>();
        fieldInfo.put("date", JsonNodeType.STRING);
        fieldInfo.put("base", JsonNodeType.STRING);
        fieldInfo.put("USD", JsonNodeType.NUMBER);
        return fieldInfo;
    }

    protected JsonNode getNode(JsonParser jsonParser) throws FXRateException {
        try {
            return checkNode(jsonParser, fieldMap);
        } catch (IOException e) {
            log.error(e.toString());
            throw new FXRateException(e.getMessage());
        }
    }

    protected Date getDate(JsonNode node) throws FXRateException {
        try {
            return formatter.parse(node.get("date").asText());
        } catch (ParseException e) {
            log.error(e.toString());
            throw new FXRateException("Could not parse date from FXRate API.");
        }
    }
}
