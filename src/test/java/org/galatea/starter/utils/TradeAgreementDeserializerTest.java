package org.galatea.starter.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.runtime.Source;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.TradeAgreement;
import org.joda.money.BigMoney;
import org.junit.Before;
import org.junit.Test;

import static org.galatea.starter.ASpringTest.readData;
import static org.junit.Assert.*;

@Slf4j
public class TradeAgreementDeserializerTest {

    private ObjectMapper mapper;
    private TradeAgreementDeserializer deserializer;
    private DeserializationContext context;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        deserializer = new TradeAgreementDeserializer(TradeAgreement.class);
        context = mapper.getDeserializationContext();
    }

    @Test
    public void testDeserialize() throws Exception {
        JsonParser jsonParser = mapper.getFactory().createParser("{}");
        TradeAgreement agreement = deserializer.deserialize(jsonParser, context);

        assertEquals(agreement.getInstrument(),"IBM");
        assertEquals(agreement.getInternalParty(), "INT-1");
        assertEquals(agreement.getExternalParty(), "EXT-1");
        assertEquals(agreement.getBuySell(), "B");
        assertEquals(agreement.getQty(), new Double(100.0));
        assertEquals(agreement.getProceeds(), BigMoney.parse("GBP 100"));
    }

}
