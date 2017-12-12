package org.galatea.starter.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.domain.TradeAgreementException;
import org.joda.money.BigMoney;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
        ArrayList<String> incorrectFields = new ArrayList<>();
        HashMap<String, JsonNodeType> fieldInfo = getFieldInfo();

        for (String key: fieldInfo.keySet()){
            if (node.has(key)){
                if (!node.get(key).getNodeType().equals(fieldInfo.get(key))) {
                    incorrectFields.add(key);
                }
            } else {
                missingFields.add(key);
            }
        }

        // More eloquent way of doing this?
        if (!missingFields.isEmpty() && !incorrectFields.isEmpty()) {
            throw new TradeAgreementException(
                String.format("Posted agreement did not contain: %s", missingFields.toString())
                    + String.format("Posted agreement had the following invalid field types: %s ", incorrectFields.toString()));
        } else if (!missingFields.isEmpty()) {
            throw new TradeAgreementException(
                String.format("Posted agreement did not contain: %s", missingFields.toString()));
        } else if (!incorrectFields.isEmpty()) {
            throw new TradeAgreementException(
                String.format("Posted agreement had the following invalid field types: %s ", incorrectFields.toString()));
        }

        return node;
    }

    private HashMap<String, JsonNodeType> getFieldInfo() {
        HashMap<String, JsonNodeType> fieldInfo = new HashMap<>();
        fieldInfo.put("instrument", JsonNodeType.STRING);
        fieldInfo.put("internalParty", JsonNodeType.STRING);
        fieldInfo.put("externalParty", JsonNodeType.STRING);
        fieldInfo.put("buySell", JsonNodeType.STRING);
        fieldInfo.put("qty", JsonNodeType.NUMBER);
        fieldInfo.put("proceeds", JsonNodeType.STRING);
        return fieldInfo;
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
