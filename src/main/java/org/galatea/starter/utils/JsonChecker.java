package org.galatea.starter.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.galatea.starter.domain.TradeAgreementException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class JsonChecker {

    public JsonNode getNode(JsonParser jsonParser, HashMap<String, JsonNodeType> fieldInfo) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        ArrayList<String> missingFields = new ArrayList<>();
        ArrayList<String> incorrectFields = new ArrayList<>();

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
            throw new IOException(
                    String.format("Received JSON did not contain: %s", missingFields.toString())
                            + String.format("Received JSON had the following invalid field types: %s ", incorrectFields.toString()));
        } else if (!missingFields.isEmpty()) {
            throw new IOException(
                    String.format("Received JSON did not contain: %s", missingFields.toString()));
        } else if (!incorrectFields.isEmpty()) {
            throw new IOException(
                    String.format("Received JSON had the following invalid field types: %s ", incorrectFields.toString()));
        }

        return node;

    }
}
