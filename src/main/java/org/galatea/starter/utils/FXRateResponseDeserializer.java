package org.galatea.starter.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.FXRateResponse;
import org.joda.money.CurrencyUnit;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class FXRateResponseDeserializer extends StdDeserializer<FXRateResponse> {

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
                System.out.println(fieldName);

                jsonToken = jsonParser.nextToken();

                // This should probably be a switch
                if("base".equals(fieldName)){
                    baseCurrency = CurrencyUnit.of(jsonParser.getValueAsString());
                } else if ("date".equals(fieldName)){
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        validOn = formatter.parse(jsonParser.getValueAsString());
                    } catch (ParseException e) {
                        // Do something with this exception
                    }
                } else if ("USD".equals(fieldName)) {
                    exchangeRate = BigDecimal.valueOf(jsonParser.getValueAsDouble());
                }
            }
        }
        if (baseCurrency != null && validOn != null && exchangeRate != null) {
            return new FXRateResponse(baseCurrency, validOn, exchangeRate);
        } else {
            log.info("It broke");
            return null; // What happens if this returns null?
        }
    }
}
