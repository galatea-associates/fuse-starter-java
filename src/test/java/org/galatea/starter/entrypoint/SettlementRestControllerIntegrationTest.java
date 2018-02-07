package org.galatea.starter.entrypoint;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import org.galatea.starter.domain.FxRateResponse;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.utils.deserializers.FxRateResponseDeserializer;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

  /**
   * Need this as we don't normally serialize TradeAgreements
   */
  class TradeAgreementSerializer extends StdSerializer<TradeAgreement> {

    public TradeAgreementSerializer() {
      super(TradeAgreement.class);
    }

    @Override
    public void serialize(TradeAgreement tradeAgreement, JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider) throws IOException {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField("instrument", tradeAgreement.getInstrument());
      jsonGenerator.writeStringField("internalParty", tradeAgreement.getInternalParty());
      jsonGenerator.writeStringField("externalParty", tradeAgreement.getExternalParty());
      jsonGenerator.writeStringField("buySell", tradeAgreement.getBuySell());
      jsonGenerator.writeNumberField("qty", tradeAgreement.getQty());
      jsonGenerator.writeStringField("proceeds", writeBigMoney(tradeAgreement.getProceeds()));
      jsonGenerator.writeEndObject();
    }

    public String writeBigMoney(BigMoney money) {
      return money.getCurrencyUnit().toString() + " " + money.getAmount().toString();
    }
  }

  /**
   * Need this as we don't normally deserialize SettlementMissions
   */
  class SettlementMissionDeserializer extends StdDeserializer {

    public SettlementMissionDeserializer() {
      super(SettlementMission.class);
    }

    @Override
    public SettlementMission deserialize(JsonParser jsonParser,
        DeserializationContext deserializationContext)
        throws IOException {

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

  private String address;

  @Before
  public void setUp() throws Exception {
    address = System.getProperty("location");
  }

  @Test
  public void testMissionCreation() throws Exception {
    // Create some TradeAgreements and POST them to the application
    List<Long> createdMissionIds = postTradeAgreements();
    log.info("Created mission IDs: %s", createdMissionIds.toString());

    // Query the GET endpoint for the created SettlementMissions
    List<SettlementMission> actualMissions = getActualMissions(createdMissionIds);
    log.info("Created missions: %s", actualMissions.toString());

    // Create the SettlementMissions that should be there
    List<SettlementMission> expectedMissions = getExpectedMissions(createdMissionIds,
        getCurrentExchangeRate());

    assertEquals(expectedMissions, actualMissions);
  }

  public List<Long> postTradeAgreements() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(
        new SimpleModule().addSerializer(TradeAgreement.class, new TradeAgreementSerializer()));

    FuseServer fusePostServer = Feign.builder()
        .decoder(new GsonDecoder())
        .encoder(new JacksonEncoder(mapper))
        .target(FuseServer.class, address);

    List<String> missionPaths = fusePostServer.sendTradeAgreement(getTradeAgreements());

    return missionPaths.stream()
        .map(s -> Long.parseLong(s.split("/")[3]))
        .sorted(Long::compare)
        .collect(Collectors.toList());
  }

  public TradeAgreement[] getTradeAgreements() throws Exception {
    return new TradeAgreement[]{
        TradeAgreement.builder()
            .instrument("IBM")
            .internalParty("INT-1")
            .externalParty("EXT-1")
            .buySell("B")
            .qty(100.0)
            .proceeds(BigMoney.parse("GBP 100"))
            .build(),
        TradeAgreement.builder()
            .instrument("GOOGL")
            .internalParty("INT-1")
            .externalParty("EXT-2")
            .buySell("B")
            .qty(200.0)
            .proceeds(BigMoney.parse("GBP 1000"))
            .build(),
    };
  }

  public BigDecimal getCurrentExchangeRate() throws Exception {
    ObjectMapper fxMapper = new ObjectMapper();
    fxMapper.registerModule(
        new SimpleModule().addDeserializer(FxRateResponse.class, new FxRateResponseDeserializer()));

    FxRateServer fxServer = Feign.builder()
        .decoder(new JacksonDecoder(fxMapper))
        .target(FxRateServer.class, "http://api.fixer.io");

    log.info("Querying http://api.fixer.io for exchange rate...");
    FxRateResponse fxRateResponse = fxServer.getRate("GBP");
    log.info("Received FxRateResponse: %s", fxRateResponse.toString());
    return fxRateResponse.getExchangeRate();
  }

  public List<SettlementMission> getActualMissions(List<Long> ids) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new SimpleModule()
        .addDeserializer(SettlementMission.class, new SettlementMissionDeserializer()));

    FuseServer fuseGetServer = Feign.builder()
        .decoder(new JacksonDecoder(mapper))
        .encoder(new GsonEncoder())
        .target(FuseServer.class, address);

    return ids.stream()
        .map(fuseGetServer::getSettlementMission)
        .collect(Collectors.toList());
  }

  public List<SettlementMission> getExpectedMissions(List<Long> ids,
      BigDecimal currentExchangeRate) {
    return Arrays.asList(
        SettlementMission.builder()
            .id(ids.get(0))
            .instrument("IBM")
            .externalParty("EXT-1")
            .depot("DTC")
            .direction("REC")
            .qty(100.0)
            .proceeds(BigMoney.of(CurrencyUnit.of("GBP"), 100d))
            .usdProceeds(BigMoney.of(CurrencyUnit.of("GBP"), 100d)
                .convertedTo(CurrencyUnit.USD, currentExchangeRate))
            .build(),
        SettlementMission.builder()
            .id(ids.get(1))
            .instrument("GOOGL")
            .externalParty("EXT-2")
            .depot("DTC")
            .direction("REC")
            .qty(200.0)
            .proceeds(BigMoney.of(CurrencyUnit.of("GBP"), 1000d))
            .usdProceeds(BigMoney.of(CurrencyUnit.of("GBP"), 1000d)
                .convertedTo(CurrencyUnit.USD, currentExchangeRate))
            .build());
  }
}
