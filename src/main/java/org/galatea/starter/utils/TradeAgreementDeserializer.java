package org.galatea.starter.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.domain.TradeAgreementException;
import org.joda.money.BigMoney;

import java.io.IOException;
import java.util.ArrayList;

// http://www.baeldung.com/jackson-deserialization
@Slf4j
public class TradeAgreementDeserializer extends StdDeserializer<TradeAgreement> {

    public TradeAgreementDeserializer() {
        this(null);
    }

    public TradeAgreementDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public TradeAgreement deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws TradeAgreementException, IOException {
        JsonNode node = getJsonNode(jsonParser);
        BigMoney proceeds = getProceeds(node);

        return TradeAgreement.builder()
            .instrument(node.get("instrument").asText())
            .internalParty(node.get("internalParty").asText())
            .externalParty(node.get("externalParty").asText())
            .buySell(node.get("buySell").asText())
            .qty(node.get("qty").asDouble())
            .proceeds(proceeds)
            .build();
    }

    private JsonNode getJsonNode(JsonParser jsonParser) throws TradeAgreementException {
        try {
            return checkJsonNode(jsonParser.getCodec().readTree(jsonParser));
        } catch (IOException e) {
            log.error(e.toString());
            throw new TradeAgreementException("Posted JSON could not be deserialized.");
        }
    }

    // Change to lambda expression
    private JsonNode checkJsonNode(JsonNode node) throws TradeAgreementException {
        ArrayList<String> missingFields = new ArrayList<>();
        String[] expectedFields = {"instrument", "internalParty", "externalParty", "buySell", "qty", "proceeds"};

        for (String key: expectedFields){
            if (!node.has(key)){
                missingFields.add(key);
            }
        }

        if(!missingFields.isEmpty()) {
            throw new TradeAgreementException(String.format("Posted agreement did not contain: %s", missingFields.toString()));
        }

        return node;
    }

    private BigMoney getProceeds(JsonNode node) throws TradeAgreementException {
        try {
            return BigMoney.parse(node.get("proceeds").asText());
        } catch (IllegalArgumentException e) {
            log.error(e.toString());
            throw new TradeAgreementException("Posted agreement contained invalid proceeds.");
        }
    }
}
