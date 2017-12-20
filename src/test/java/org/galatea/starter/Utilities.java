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

    public static TradeAgreement getTradeAgreement() throws IOException {
        String agreementJson = readData("Test_IBM_Agreement.json").replace("\n", "").replace("[", "").replace("]", "");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(agreementJson);

        return TradeAgreement.builder()
                .instrument(node.get("instrument").asText())
                .internalParty(node.get("internalParty").asText())
                .externalParty(node.get("externalParty").asText())
                .buySell(node.get("buySell").asText())
                .qty(node.get("qty").asDouble())
                .proceeds(BigMoney.parse(node.get("proceeds").asText()))
                .build();
    }
}
