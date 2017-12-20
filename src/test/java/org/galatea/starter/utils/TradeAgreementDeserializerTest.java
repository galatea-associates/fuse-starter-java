package org.galatea.starter.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.utils.deserializers.TradeAgreementDeserializer;
import org.joda.money.BigMoney;
import org.junit.Before;
import org.junit.Test;

import static org.galatea.starter.ASpringTest.readData;
import static org.junit.Assert.assertEquals;

@Slf4j
public class TradeAgreementDeserializerTest {

//    Codec of JsonParser: com.fasterxml.jackson.databind.ObjectMapper@60aee4ca

    private ObjectMapper mapper;
    private TradeAgreementDeserializer deserializer;
    private DeserializationContext context;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        deserializer = new TradeAgreementDeserializer();
        context = mapper.getDeserializationContext();
    }

    @Test
    public void testDeserialize() throws Exception {
        String agreementJson = readData("Test_IBM_Agreement.json").replace("\n", "").replace("[", "").replace("]", "");
        JsonParser jsonParser = mapper.getFactory().createParser(agreementJson);
        //System.out.println(jsonParser.readValueAsTree().toString());
        TradeAgreement agreement = deserializer.deserialize(jsonParser, context);
        
        
        assertEquals(agreement.getInstrument(),"IBM");
        assertEquals(agreement.getInternalParty(), "INT-1");
        assertEquals(agreement.getExternalParty(), "EXT-1");
        assertEquals(agreement.getBuySell(), "B");
        assertEquals(agreement.getQty(), new Double(100.0));
        assertEquals(agreement.getProceeds(), BigMoney.parse("GBP 100"));
    }

}
