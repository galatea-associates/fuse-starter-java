package org.galatea.starter.entrypoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementMissionProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementResponseProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessages;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionList;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionMessage;
import org.galatea.starter.entrypoint.messagecontracts.SettlementResponseMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessages;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;


@RequiredArgsConstructor
@Slf4j
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class SettlementRestControllerIntegrationTest {


  @Value("${fuse-host.url}")
  private String FuseHostName;

  interface FuseServer {

    @RequestLine("POST /settlementEngine")
    @Headers("Content-Type: application/json")
    SettlementResponseMessage sendTradeAgreementJson(TradeAgreementMessages tradeAgreements);

    @RequestLine("GET /settlementEngine/mission/{id}")
    SettlementMissionMessage getSettlementMissionJson(@Param("id") Long id);

    @RequestLine("GET /settlementEngine/missions?ids={ids}")
    SettlementMissionList getSettlementMissionsJson(@Param("ids") String ids);

    @RequestLine("POST /settlementEngine")
    @Headers({"Content-Type: application/x-protobuf", "Accept: application/x-protobuf"})
    SettlementResponseProtoMessage sendTradeAgreementProto(
        TradeAgreementProtoMessages tradeAgreements);

    @RequestLine("GET /settlementEngine/mission/{id}")
    @Headers({"Accept: application/x-protobuf"})
    SettlementMissionProtoMessage getSettlementMissionProto(@Param("id") Long id);

  }

  @Test
  public void testMissionCreationProto() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      fuseHostName = FuseHostName;
    }

    ObjectFactory<HttpMessageConverters> messageConverters = () -> new HttpMessageConverters(
        new ProtobufHttpMessageConverter());

    FuseServer fuseServer = Feign.builder()
        .decoder(new SpringDecoder(messageConverters))
        .encoder(new SpringEncoder(messageConverters))
        .target(FuseServer.class, fuseHostName);

    String instrument = "IBM";
    String external1 = "ecp-1";
    String external2 = "ecp-2";
    double quantity1 = 4500d;
    double quantity2 = 4600d;

    TradeAgreementProtoMessages messages = TradeAgreementProtoMessages.newBuilder().addMessage(
        TradeAgreementProtoMessage.newBuilder().setInstrument(instrument)
            .setInternalParty("icp-1")
            .setExternalParty(external1).setBuySell("B").setQty(quantity1).build()).addMessage(
        TradeAgreementProtoMessage.newBuilder().setInstrument(instrument)
            .setInternalParty("icp-2")
            .setExternalParty(external2).setBuySell("B").setQty(quantity2).build()).build();

    SettlementResponseProtoMessage missionPaths = fuseServer.sendTradeAgreementProto(messages);

    log.info("created missions: {}", missionPaths);

    SettlementMissionProtoMessage.Builder b1 = SettlementMissionProtoMessage.newBuilder()
        .setDepot("DTC").setInstrument(instrument).setExternalParty(external1).setDirection("REC")
        .setQty(quantity1).setVersion(0L);

    SettlementMissionProtoMessage.Builder b2 = SettlementMissionProtoMessage.newBuilder()
        .setDepot("DTC").setInstrument(instrument).setExternalParty(external2).setDirection("REC")
        .setQty(quantity2).setVersion(0L);

    assertEquals(2, missionPaths.getSpawnedMissionPathsList().size());

    for (String missionPath : missionPaths.getSpawnedMissionPathsList()) {
      Long missionId = Long.parseLong(missionPath.split("/")[3]); // brittle assumption...
      SettlementMissionProtoMessage settlementMission = fuseServer
          .getSettlementMissionProto(missionId);
      log.info("fetched mission: {}", settlementMission);

      assertTrue(b1.setId(missionId).build().equals(settlementMission)
          || b2.setId(missionId).build().equals(settlementMission));
    }
  }

  @Test
  public void testMissionCreationJson() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      fuseHostName = FuseHostName;
    }

    ObjectFactory<HttpMessageConverters> messageConverters = () -> new HttpMessageConverters(
        new MappingJackson2HttpMessageConverter());

    FuseServer fuseServer = Feign.builder()
        .decoder(new SpringDecoder(messageConverters))
        .encoder(new SpringEncoder(messageConverters))
        .target(FuseServer.class, fuseHostName);

    String instrument = "IBM";
    String external1 = "ecp-1";
    String external2 = "ecp-2";
    double quantity1 = 4500d;
    double quantity2 = 4600d;

    TradeAgreementMessages messages = TradeAgreementMessages.builder().agreement(
        TradeAgreementMessage.builder().instrument(instrument).internalParty("icp-1")
            .externalParty(external1).buySell("B").qty(quantity1).build()).agreement(
        TradeAgreementMessage.builder().instrument(instrument).internalParty("icp-2")
            .externalParty(external2).buySell("B").qty(quantity2).build()).build();

    SettlementResponseMessage missionPaths = fuseServer.sendTradeAgreementJson(messages);

    log.info("created missions: {}", missionPaths);

    SettlementMissionMessage.SettlementMissionMessageBuilder b1 = SettlementMissionMessage.builder()
        .depot("DTC").instrument(instrument).externalParty(external1).direction("REC")
        .qty(quantity1).version(0L);

    SettlementMissionMessage.SettlementMissionMessageBuilder b2 = SettlementMissionMessage.builder()
        .depot("DTC").instrument(instrument).externalParty(external2).direction("REC")
        .qty(quantity2).version(0L);

    assertEquals(2, missionPaths.getSpawnedMissions().size());

    // Fetch individual missions
    List<String> missionIds = new ArrayList<>();
    for (String missionPath : missionPaths.getSpawnedMissions()) {
      Long missionId = Long.parseLong(missionPath.split("/")[3]); // brittle assumption...
      // Convert to Strings here to avoid needing to convert a bunch of Longs to Strings later
      missionIds.add(missionId.toString());
      SettlementMissionMessage settlementMission = fuseServer.getSettlementMissionJson(missionId);
      log.info("fetched mission: {}", settlementMission);

      assertTrue(b1.id(missionId).build().equals(settlementMission)
          || b2.id(missionId).build().equals(settlementMission));
    }

    // Fetch multiple missions
    SettlementMissionList settlementMissions = fuseServer.getSettlementMissionsJson(
        String.join(",", missionIds));
    assertEquals(2, settlementMissions.getSettlementMissions().size());
    assertEquals(missionIds.get(0),
        settlementMissions.getSettlementMissions().get(0).getId().toString());
    assertEquals(missionIds.get(1),
        settlementMissions.getSettlementMissions().get(1).getId().toString());
  }
}
