package org.galatea.starter.utils.deserializers;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.HashMap;

public abstract class FuseJsonDeserializer<T> extends StdDeserializer<T> {

    public static HashMap<String, JsonNodeType> jsonInfo;

    public FuseJsonDeserializer(Class<?>vc) {
        super(vc);
        jsonInfo = getFieldInfo();
    }

    @Override
    public abstract T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException;

    public abstract HashMap<String, JsonNodeType> getFieldInfo();

}
