package org.galatea.starter.utils.deserializers;

import static org.galatea.starter.TestUtilities.getJsonFromFile;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import org.galatea.starter.domain.FxRateResponse;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

public class FxRateResponseDeserializerTest {

  private FxRateResponseDeserializer deserializer;
  private ObjectMapper mapper;
  private DeserializationContext context;
  private String responseJson;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    deserializer = new FxRateResponseDeserializer();
    mapper = new ObjectMapper();
    context = mapper.getDeserializationContext();
    responseJson = getJsonFromFile("FxRateResponse/Correct_FX_Response.json");
  }

  @Test
  public void testDeserialize() throws Exception {
    JsonParser jsonParser = mapper.getFactory().createParser(responseJson);
    FxRateResponse response = deserializer.deserialize(jsonParser, context);

    assertEquals(BigDecimal.valueOf(1.3467), response.getExchangeRate());
    assertEquals(CurrencyUnit.GBP, response.getBaseCurrency());
    assertEquals(LocalDate.parse("2017-11-30"), response.getValidOn());
  }

  @Test
  public void testDeserializeIncorrectFields() throws Exception {
    expectedException.expect(IOException.class);
    expectedException.expectMessage("Received JSON had the following invalid field types: [USD]");

    JsonParser jsonParser =
        mapper
            .getFactory()
            .createParser(getJsonFromFile("FxRateResponse/Incorrect_Fields_FX_Response.json"));
    FxRateResponse response = deserializer.deserialize(jsonParser, context);
  }

  @Test
  public void testDeserializeMissingFields() throws Exception {
    expectedException.expect(IOException.class);
    expectedException.expectMessage("Received JSON did not contain: [date]");

    JsonParser jsonParser =
        mapper
            .getFactory()
            .createParser(getJsonFromFile("FxRateResponse/Missing_Field_FX_Response.json"));
    FxRateResponse response = deserializer.deserialize(jsonParser, context);
  }

  @Test
  public void testDeserializeMissingIncorrectFields() throws Exception {
    expectedException.expect(IOException.class);
    expectedException.expectMessage(
        "Received JSON did not contain: [base] & had the following invalid field types: [date]");

    JsonParser jsonParser =
        mapper
            .getFactory()
            .createParser(
                getJsonFromFile("FxRateResponse/Missing_Incorrect_Fields_FX_Response.json"));
    FxRateResponse response = deserializer.deserialize(jsonParser, context);
  }

  @Test
  public void testGetRootFieldMap() {
    HashMap<String, JsonNodeType> fieldMap = deserializer.getRootFieldMap();
    HashMap<String, JsonNodeType> expected = new HashMap<>();
    expected.put("date", JsonNodeType.STRING);
    expected.put("base", JsonNodeType.STRING);
    expected.put("rates", JsonNodeType.OBJECT);

    assertEquals(expected, fieldMap);
  }

  @Test
  public void testGetRatesFieldMap() {
    HashMap<String, JsonNodeType> fieldMap = deserializer.getRatesFieldMap();
    HashMap<String, JsonNodeType> expected = new HashMap<>();
    expected.put("USD", JsonNodeType.NUMBER);

    assertEquals(expected, fieldMap);
  }

  @Test
  public void testGetDate() throws Exception {
    JsonNode node = mapper.readTree(responseJson);
    LocalDate validOn = deserializer.getDate(node);

    assertEquals(LocalDate.parse("2017-11-30"), validOn);
  }

  @Test
  public void testGetDateIOException() throws Exception {
    expectedException.expect(IOException.class);
    expectedException.expectMessage("Unable to parse date from FX Rate API.");

    String incorrectResponseJson =
        getJsonFromFile("FxRateResponse/Incorrect_Fields_FX_Response.json");
    JsonNode node = mapper.readTree(incorrectResponseJson);
    LocalDate validOn = deserializer.getDate(node);
  }
}
