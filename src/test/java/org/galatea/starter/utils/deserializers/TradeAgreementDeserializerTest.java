package org.galatea.starter.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.domain.TradeAgreementException;
import org.galatea.starter.utils.deserializers.TradeAgreementDeserializer;
import org.joda.money.BigMoney;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.galatea.starter.Utilities.getTradeAgreementJsonFromFile;
import static org.galatea.starter.Utilities.getTradeAgreementNodeFromFile;
import static org.junit.Assert.assertEquals;

@Slf4j
public class TradeAgreementDeserializerTest {

    private TradeAgreementDeserializer deserializer;
    private ObjectMapper mapper;
    private DeserializationContext context;
    private String agreementJson;

    @Before
    public void setUp() throws Exception {
        deserializer = new TradeAgreementDeserializer();
        mapper = new ObjectMapper();
        context = mapper.getDeserializationContext();
        agreementJson = getTradeAgreementJsonFromFile("Correct_IBM_Agreement.json");
    }

    @Test
    public void testDeserialize() throws Exception {
        JsonParser jsonParser = mapper.getFactory().createParser(agreementJson);
        TradeAgreement agreement = deserializer.deserialize(jsonParser, context);

        assertEquals(agreement.getInstrument(),"IBM");
        assertEquals(agreement.getInternalParty(), "INT-1");
        assertEquals(agreement.getExternalParty(), "EXT-1");
        assertEquals(agreement.getBuySell(), "B");
        assertEquals(agreement.getQty(), new Double(100.0));
        assertEquals(agreement.getProceeds(), BigMoney.parse("GBP 100"));
    }

    @Test
    public void testGetFieldMap() {
        HashMap<String, JsonNodeType> fieldMap = deserializer.getFieldMap();
        HashMap<String, JsonNodeType> expectedMap = new HashMap<>();
        expectedMap.put("instrument", JsonNodeType.STRING);
        expectedMap.put("internalParty", JsonNodeType.STRING);
        expectedMap.put("externalParty", JsonNodeType.STRING);
        expectedMap.put("buySell", JsonNodeType.STRING);
        expectedMap.put("qty", JsonNodeType.NUMBER);
        expectedMap.put("proceeds", JsonNodeType.STRING);

        assertEquals(fieldMap, expectedMap);
    }

    @Test
    public void testGetCheckedNode() throws Exception {
        JsonParser jsonParser = mapper.getFactory().createParser(agreementJson);
        JsonNode node = deserializer.getCheckedNode(jsonParser);

        assertEquals(node, getTradeAgreementNodeFromFile("Correct_IBM_Agreement.json"));
    }

    @Test
    public void testGetProceeds() throws Exception {
        JsonNode node = mapper.readTree(agreementJson);
        BigMoney proceeds = deserializer.getProceeds(node);

        assertEquals(proceeds, BigMoney.parse("GBP 100"));
    }

    @Test(expected = TradeAgreementException.class)
    public void testGetProceedsTradeAgreementException() throws Exception {
        String incorrectAgreementJson = getTradeAgreementJsonFromFile("Incorrect_Fields_IBM_Agreement.json");
        JsonNode node = mapper.readTree(incorrectAgreementJson);
        BigMoney proceeds = deserializer.getProceeds(node);
    }
}
