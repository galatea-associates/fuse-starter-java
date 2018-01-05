package org.galatea.starter.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class FuseDeserializer extends StdDeserializer {

  protected FuseDeserializer(Class vc) {
    super(vc);
  }

  protected JsonNode getAndCheckRootNode(
      JsonParser jsonParser, HashMap<String, JsonNodeType> fieldInfo) throws IOException {
    return checkNode(jsonParser.readValueAsTree(), fieldInfo);
  }

  protected JsonNode checkNode(JsonNode node, HashMap<String, JsonNodeType> fieldInfo)
      throws IOException {

    List<String> missingFields = fieldInfo.keySet()
        .stream()
        .filter(k -> !node.has(k))
        .collect(Collectors.toList());

    List<String> incorrectFields = fieldInfo.keySet()
        .stream()
        .filter(k -> node.has(k) && !node.get(k).getNodeType().equals(fieldInfo.get(k)))
        .collect(Collectors.toList());

    if (!missingFields.isEmpty() && !incorrectFields.isEmpty()) {
      throw new IOException(String
          .format("Received JSON did not contain: %s & had the following invalid field types: %s",
              missingFields, incorrectFields));
    } else if (!missingFields.isEmpty()) {
      throw new IOException(String.format("Received JSON did not contain: %s", missingFields));
    } else if (!incorrectFields.isEmpty()) {
      throw new IOException(String
          .format("Received JSON had the following invalid field types: %s", incorrectFields));
    }

    return node;
  }
}
