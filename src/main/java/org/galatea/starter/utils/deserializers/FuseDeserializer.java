package org.galatea.starter.utils.deserializers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.util.function.Function;

public abstract class FuseDeserializer extends StdDeserializer {

  protected FuseDeserializer(Class vc) {
    super(vc);
  }

  protected <T> T getIfValid(JsonNode node, String key, JsonNodeType type,
      Function<JsonNode, T> function) {
    return node.has(key) && node.get(key).getNodeType().equals(type) ? function.apply(node) : null;
  }
}



