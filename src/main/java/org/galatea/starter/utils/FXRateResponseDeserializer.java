package org.galatea.starter.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.FXRateException;
import org.galatea.starter.domain.FXRateResponse;
import org.joda.money.CurrencyUnit;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

;

@Slf4j
public class FXRateResponseDeserializer extends StdDeserializer {

    public FXRateResponseDeserializer(Class<?>vc) {
        super(vc);
    }

    // http://tutorials.jenkov.com/java-json/jackson-objectmapper.html
    @Override
    public FXRateResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        CurrencyUnit baseCurrency = null;
        Date validOn = null;
        BigDecimal exchangeRate = null;

        while(!jsonParser.isClosed()){
            JsonToken jsonToken = jsonParser.nextToken();

            if(JsonToken.FIELD_NAME.equals(jsonToken)){
                String fieldName = jsonParser.getCurrentName();

                jsonToken = jsonParser.nextToken();

                try {
                    switch (fieldName) {
                        case "base":
                            baseCurrency = CurrencyUnit.of(jsonParser.getValueAsString());
                            break;
                        case "date":
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            validOn = formatter.parse(jsonParser.getValueAsString());
                            break;
                        case "USD":
                            exchangeRate = BigDecimal.valueOf(jsonParser.getValueAsDouble());
                            break;
                    }
                } catch (IOException | ParseException e) {
                    log.error(e.toString());
                    throw new FXRateException("Failed to deserialize response from pricing API.");
                }
            }
        }

        if (baseCurrency == null || validOn == null || exchangeRate == null) {
            log.error("Failed to deserialize response from pricing API. Parsed variables were: baseCurrency:{} validOn:{} exchangeRate:{}", baseCurrency, validOn, exchangeRate);
            throw new FXRateException("Failed to deserialize response from FX pricing API.");
        }
        return new FXRateResponse(baseCurrency, validOn, exchangeRate);
    }
}
