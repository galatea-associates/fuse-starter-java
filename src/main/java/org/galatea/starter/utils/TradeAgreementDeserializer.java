package org.galatea.starter.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.RequiredArgsConstructor;
import org.galatea.starter.domain.TradeAgreement;
import org.joda.money.BigMoney;

import java.io.IOException;

// http://www.baeldung.com/jackson-deserialization
public class TradeAgreementDeserializer extends StdDeserializer<TradeAgreement> {

    public TradeAgreementDeserializer() {
        this(null);
    }

    public TradeAgreementDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public TradeAgreement deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        return TradeAgreement.builder()
            .instrument(node.get("instrument").asText())
            .internalParty(node.get("internalParty").asText())
            .externalParty(node.get("externalParty").asText())
            .buySell(node.get("buySell").asText())
            .qty(node.get("qty").asDouble())
            .proceeds(BigMoney.parse(node.get("proceeds").asText()))
            .build();
    }
}
