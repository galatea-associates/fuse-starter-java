
package org.galatea.starter.entrypoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionMessage;
import org.galatea.starter.entrypoint.messagecontracts.SettlementResponseMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessages;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;


@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class SettlementRestControllerIntegrationTest {

  private ObjectFactory<HttpMessageConverters> messageConverters;

  @Value("${fuse-host.url}")
  private String FuseHostName;

  interface FuseServer {

    @RequestLine("POST /settlementEngine")
    @Headers({"Content-Type: application/x-protobuf", "Accept: application/x-protobuf"})
    SettlementResponseMessage sendTradeAgreement(TradeAgreementMessages tradeAgreements);

    @RequestLine("GET /settlementEngine/mission/{id}")
    SettlementMissionMessage getSettlementMission(@Param("id") Long id);
  }

  @Before
  public void setUp() {
    messageConverters = () -> new HttpMessageConverters(new ProtobufHttpMessageConverter());
  }

  @Test
  public void testMissionCreation() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      fuseHostName = FuseHostName;
    }

    FuseServer fuseServer = Feign.builder()
        .decoder(new SpringDecoder(messageConverters))
        .encoder(new SpringEncoder(messageConverters))
        .target(FuseServer.class, fuseHostName);

    TradeAgreementMessages messages = TradeAgreementMessages.builder().agreement(
        TradeAgreementMessage.builder().id(4000L).instrument("IBM").internalParty("icp-1")
            .externalParty("ecp-1").buySell("B").qty(4500d).build()).agreement(
        TradeAgreementMessage.builder().id(40001L).instrument("IBM").internalParty("icp-2")
            .externalParty("ecp-2").buySell("B").qty(4600d).build()).build();

    SettlementResponseMessage missionPaths = fuseServer.sendTradeAgreement(messages);

    log.info("created missions: {}", missionPaths);

    SettlementMissionMessage.SettlementMissionMessageBuilder b1 = SettlementMissionMessage.builder()
        .depot("DTC").instrument("IBM").externalParty("ecp-1").direction("REC").qty(4500d);

    SettlementMissionMessage.SettlementMissionMessageBuilder b2 = SettlementMissionMessage.builder()
        .depot("DTC").instrument("IBM").externalParty("ecp-2").direction("REC").qty(4600d);

    assertEquals(2, missionPaths.getSpawnedMissions().size());

    for (String missionPath : missionPaths.getSpawnedMissions()) {
      Long missionId = Long.parseLong(missionPath.split("/")[3]); // brittle assumption...
      SettlementMissionMessage settlementMission = fuseServer.getSettlementMission(missionId);
      log.info("fetched mission: {}", settlementMission);

      assertTrue(b1.id(missionId).build().equals(settlementMission)
          || b2.id(missionId).build().equals(settlementMission));
    }
  }
}
