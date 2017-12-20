package org.galatea.starter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.TradeAgreement;
import org.joda.money.BigMoney;

import java.io.IOException;

import static org.galatea.starter.ASpringTest.readData;

@Slf4j
public class Utilities {

    public static TradeAgreement getTradeAgreement() throws Exception {
        JsonNode node = getTradeAgreementNodeFromFile("Correct_IBM_Agreement.json");

        return TradeAgreement.builder()
                .instrument(node.get("instrument").asText())
                .internalParty(node.get("internalParty").asText())
                .externalParty(node.get("externalParty").asText())
                .buySell(node.get("buySell").asText())
                .qty(node.get("qty").asDouble())
                .proceeds(BigMoney.parse(node.get("proceeds").asText()))
                .build();
    }

    public static JsonNode getTradeAgreementNodeFromFile(String fileName) throws Exception {
        String agreementJson = getTradeAgreementJsonFromFile(fileName);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(agreementJson);
    }

    public static String getTradeAgreementJsonFromFile(String fileName) throws Exception {
        return readData(fileName).replace("\n", "").replace("[", "").replace("]", "");
    }

}
