package org.galatea.starter.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;

public class JsonDeserializeTest {

  @Test
  public void deserialize() {
    ObjectMapper mapper = new ObjectMapper();
    //Convert JSON string to Object
    String alphaVantageJson = "{\n"
        + "   \"Meta Data\": {\n"
        + "        \"1. Information\": \"Daily Prices (open, high, low, close) and Volumes\",\n"
        + "        \"2. Symbol\": \"MSFT\",\n"
        + "        \"3. Last Refreshed\": \"2020-03-09\",\n"
        + "        \"4. Output Size\": \"Compact\",\n"
        + "        \"5. Time Zone\": \"US/Eastern\"\n"
        + "    },\n"
        + "    \"Time Series (Daily)\": {\n"
        + "        \"2020-03-09\": {\n"
        + "            \"1. open\": \"151.0000\",\n"
        + "            \"2. high\": \"157.7500\",\n"
        + "            \"3. low\": \"150.0000\",\n"
        + "            \"4. close\": \"150.5800\",\n"
        + "            \"5. volume\": \"69816016\"\n"
        + "        }\n"
        + "    }\n"
        + "}";
    try {
      AlphaVantageResponse response = mapper.readValue(alphaVantageJson, AlphaVantageResponse.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
