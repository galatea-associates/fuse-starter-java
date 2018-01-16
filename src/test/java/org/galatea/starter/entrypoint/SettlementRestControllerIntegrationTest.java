package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestInterceptor;
import feign.RequestLine;
import feign.RequestTemplate;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.jackson.JacksonDecoder;

import org.galatea.starter.TestUtilities;
import org.galatea.starter.domain.FxRateResponse;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.utils.deserializers.FxRateResponseDeserializer;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class SettlementRestControllerIntegrationTest {

  interface FuseServer {

    @RequestLine("POST /settlementEngine")
    @Headers("Content-Type: application/json")
    List<String> sendTradeAgreement(TradeAgreement[] jsonAgreements);

    @RequestLine("GET /settlementEngine/mission/{id}")
    SettlementMission getSettlementMission(@Param("id") Long id);
  }

  interface FxRateServer {

    @RequestLine("GET /latest?base={base}&symbols=USD")
    FxRateResponse getRate(@Param("base") String base);
  }

  class ModifyBigMoneyInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
      String body = new String(template.body());
      template.body(body.replace("{\n"
          + "      \"currency\": {\n"
          + "        \"code\": \"GBP\",\n"
          + "        \"numericCode\": 826,\n"
          + "        \"decimalPlaces\": 2\n"
          + "      },\n"
          + "      \"amount\": 100\n"
          + "    }", "\"GBP 100\""));
    }
  }

  class SettlementMissionDeserializer extends StdDeserializer {

    public SettlementMissionDeserializer() {
      super(SettlementMission.class);
    }

    @Override
    public SettlementMission deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException, JsonProcessingException {

      JsonNode node = jsonParser.readValueAsTree();

      return SettlementMission.builder()
          .id(node.get("id").asLong())
          .instrument(node.get("instrument").asText())
          .externalParty(node.get("externalParty").asText())
          .depot(node.get("depot").asText())
          .direction(node.get("direction").asText())
          .qty(node.get("qty").asDouble())
          .proceeds(BigMoney.parse(node.get("proceeds").asText()))
          .usdProceeds(BigMoney.parse(node.get("usdProceeds").asText()))
          .build();
    }
  }

  @Test
  public void testMissionCreation() throws Exception {
    // TODO: the base URL should probably be moved to a src/test/resources properties file
    FuseServer fusePostServer = Feign.builder()
        .decoder(new GsonDecoder())
        .encoder(new GsonEncoder())
        .requestInterceptor(new ModifyBigMoneyInterceptor())
        .target(FuseServer.class, "http://localhost:8080");

    List<String> missionPaths = fusePostServer.sendTradeAgreement(new TradeAgreement[]{
        TestUtilities.getTradeAgreement()
    });

    ObjectMapper fxMapper = new ObjectMapper();
    fxMapper.registerModule(
        new SimpleModule().addDeserializer(FxRateResponse.class, new FxRateResponseDeserializer()));

    FxRateServer fxServer = Feign.builder()
        .decoder(new JacksonDecoder(fxMapper))
        .target(FxRateServer.class, "http://api.fixer.io");

    FxRateResponse fxRateResponse = fxServer.getRate("GBP");

    SettlementMission expectedMission = SettlementMission.builder()
        .id(4000L)
        .instrument("IBM")
        .externalParty("ecp-1")
        .depot("DTC")
        .direction("REC")
        .qty(4500.0)
        .proceeds(BigMoney.of(CurrencyUnit.of("GBP"), 100d))
        .usdProceeds(BigMoney.of(CurrencyUnit.of("GBP"), 100d)
            .convertedTo(CurrencyUnit.USD, fxRateResponse.getExchangeRate()))
        .build();

    ObjectMapper fuseGetMapper = new ObjectMapper();
    fuseGetMapper.registerModule(new SimpleModule()
        .addDeserializer(SettlementMission.class, new SettlementMissionDeserializer()));

    FuseServer fuseGetServer = Feign.builder()
        .decoder(new JacksonDecoder(fuseGetMapper))
        .encoder(new GsonEncoder())
        .target(FuseServer.class, "http://localhost:8080");

    SettlementMission actualMission = fuseGetServer.getSettlementMission(1073L);

    System.out.println(actualMission.toString());
  }
}
