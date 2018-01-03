package org.galatea.starter.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class FuseDeserializer extends StdDeserializer {

  protected FuseDeserializer(Class vc) {
    super(vc);
  }

  protected JsonNode getAndCheckRootNode(JsonParser jsonParser,
      HashMap<String, JsonNodeType> fieldInfo) throws IOException {
    return checkNode(jsonParser.readValueAsTree(), fieldInfo);
  }

  protected JsonNode checkNode(JsonNode node, HashMap<String, JsonNodeType> fieldInfo)
      throws IOException {
    ArrayList<String> missingFields = new ArrayList<>();
    ArrayList<String> incorrectFields = new ArrayList<>();

    for (String key : fieldInfo.keySet()) {
      if (node.has(key)) {
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
              + String
              .format(" & had the following invalid field types: %s", incorrectFields.toString()));
    } else if (!missingFields.isEmpty()) {
      throw new IOException(
          String.format("Received JSON did not contain: %s", missingFields.toString()));
    } else if (!incorrectFields.isEmpty()) {
      throw new IOException(
          String.format("Received JSON had the following invalid field types: %s",
              incorrectFields.toString()));
    }

    return node;
  }
}
