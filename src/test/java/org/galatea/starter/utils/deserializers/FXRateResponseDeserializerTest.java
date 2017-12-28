package org.galatea.starter.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.galatea.starter.domain.FXRateException;
import org.galatea.starter.domain.FXRateResponse;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.galatea.starter.Utilities.getJsonFromFile;
import static org.galatea.starter.Utilities.getJsonNodeFromFile;
import static org.junit.Assert.*;

public class FXRateResponseDeserializerTest {

    private FXRateResponseDeserializer deserializer;
    private ObjectMapper mapper;
    private DeserializationContext context;
    private String responseJson;
    private SimpleDateFormat formatter;

    @Before
    public void setUp() throws Exception {
        deserializer = new FXRateResponseDeserializer();
        mapper = new ObjectMapper();
        context = mapper.getDeserializationContext();
        responseJson = getJsonFromFile("FXRateResponse/Correct_FX_Response.json");
        formatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Test
    public void testDeserialize() throws Exception {
        JsonParser jsonParser = mapper.getFactory().createParser(responseJson);
        FXRateResponse response = deserializer.deserialize(jsonParser, context);

        assertEquals(response.getExchangeRate(), BigDecimal.valueOf(1.3467));
        assertEquals(response.getBaseCurrency(), CurrencyUnit.GBP);
        assertEquals(response.getValidOn(), formatter.parse("2017-11-30"));
    }

    @Test
    public void testGetFieldMap() {
        HashMap<String, JsonNodeType> fieldMap = deserializer.getFieldMap();
        HashMap<String, JsonNodeType> expected = new HashMap<>();
        expected.put("date", JsonNodeType.STRING);
        expected.put("base", JsonNodeType.STRING);
        expected.put("USD", JsonNodeType.NUMBER);

        assertEquals(fieldMap, expected);
    }

    @Test
    public void testGetCheckedNode() throws Exception {
        JsonParser jsonParser = mapper.getFactory().createParser(responseJson);
        JsonNode node = deserializer.getCheckedNode(jsonParser);
        assertEquals(node, getJsonNodeFromFile("FXRateResponse/Correct_FX_Response.json"));
    }

    @Test
    public void testGetDate() throws Exception {
        JsonNode node = mapper.readTree(responseJson);
        Date validOn = deserializer.getDate(node);

        assertEquals(validOn, formatter.parse("2017-11-30"));
    }

    @Test(expected = FXRateException.class)
    public void testGetDateFXRateException() throws Exception {
        String incorrectResponseJson = getJsonFromFile("FXRateResponse/Incorrect_Fields_FX_Response.json");
        JsonNode node = mapper.readTree(incorrectResponseJson);
        Date validOn = deserializer.getDate(node);
    }

}