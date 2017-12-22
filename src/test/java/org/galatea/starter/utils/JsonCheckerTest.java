package org.galatea.starter.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.galatea.starter.ASpringTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.galatea.starter.Utilities.getTradeAgreementNodeFromFile;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

public class JsonCheckerTest extends ASpringTest {

    JsonParser mockJsonParser;
    HashMap<String, JsonNodeType> fieldInfo;

    @Before
    public void setUp() {

        mockJsonParser = Mockito.mock(JsonParser.class);

        HashMap<String, JsonNodeType> fieldInfo = new HashMap<>();
        fieldInfo.put("instrument", JsonNodeType.STRING);
        fieldInfo.put("internalParty", JsonNodeType.STRING);
        fieldInfo.put("externalParty", JsonNodeType.STRING);
        fieldInfo.put("buySell", JsonNodeType.STRING);
        fieldInfo.put("qty", JsonNodeType.NUMBER);
        fieldInfo.put("proceeds", JsonNodeType.STRING);
    }


    @Test
    public void testGetNode() throws Exception {

        given(mockJsonParser.getCodec().readTree(mockJsonParser))
                .willReturn(getTradeAgreementNodeFromFile("\"Correct_IBM_Agreement.json\""));

        assertEquals(getTradeAgreementNodeFromFile("\"Correct_IBM_Agreement.json\""),
                JsonChecker.getNode(mockJsonParser, fieldInfo));
    }

}