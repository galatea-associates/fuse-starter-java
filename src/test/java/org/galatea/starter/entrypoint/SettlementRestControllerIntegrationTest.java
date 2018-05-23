
package org.galatea.starter.entrypoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementMissionProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementResponseProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessages;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionMessage;
import org.galatea.starter.entrypoint.messagecontracts.SettlementResponseMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessages;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;


@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
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

    TradeAgreementProtoMessages messages = TradeAgreementProtoMessages.newBuilder().addMessage(
        TradeAgreementProtoMessage.newBuilder().setId(4000L).setInstrument("IBM")
            .setInternalParty("icp-1")
            .setExternalParty("ecp-1").setBuySell("B").setQty(4500d).build()).addMessage(
        TradeAgreementProtoMessage.newBuilder().setId(40001L).setInstrument("IBM")
            .setInternalParty("icp-2")
            .setExternalParty("ecp-2").setBuySell("B").setQty(4600d).build()).build();

    SettlementResponseProtoMessage missionPaths = fuseServer.sendTradeAgreementProto(messages);

    log.info("created missions: {}", missionPaths);

    SettlementMissionProtoMessage.Builder b1 = SettlementMissionProtoMessage.newBuilder()
        .setDepot("DTC").setInstrument("IBM").setExternalParty("ecp-1").setDirection("REC")
        .setQty(4500d);

    SettlementMissionProtoMessage.Builder b2 = SettlementMissionProtoMessage.newBuilder()
        .setDepot("DTC").setInstrument("IBM").setExternalParty("ecp-2").setDirection("REC")
        .setQty(4600d);

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

    TradeAgreementMessages messages = TradeAgreementMessages.builder().agreement(
        TradeAgreementMessage.builder().id(4000L).instrument("IBM").internalParty("icp-1")
            .externalParty("ecp-1").buySell("B").qty(4500d).build()).agreement(
        TradeAgreementMessage.builder().id(40001L).instrument("IBM").internalParty("icp-2")
            .externalParty("ecp-2").buySell("B").qty(4600d).build()).build();

    SettlementResponseMessage missionPaths = fuseServer.sendTradeAgreementJson(messages);

    log.info("created missions: {}", missionPaths);

    SettlementMissionMessage.SettlementMissionMessageBuilder b1 = SettlementMissionMessage.builder()
        .depot("DTC").instrument("IBM").externalParty("ecp-1").direction("REC").qty(4500d);

    SettlementMissionMessage.SettlementMissionMessageBuilder b2 = SettlementMissionMessage.builder()
        .depot("DTC").instrument("IBM").externalParty("ecp-2").direction("REC").qty(4600d);

    assertEquals(2, missionPaths.getSpawnedMissions().size());

    for (String missionPath : missionPaths.getSpawnedMissions()) {
      Long missionId = Long.parseLong(missionPath.split("/")[3]); // brittle assumption...
      SettlementMissionMessage settlementMission = fuseServer.getSettlementMissionJson(missionId);
      log.info("fetched mission: {}", settlementMission);

      assertTrue(b1.id(missionId).build().equals(settlementMission)
          || b2.id(missionId).build().equals(settlementMission));
    }
  }
}
