
package org.galatea.starter.entrypoint;

import static org.galatea.starter.entrypoint.messagecontracts.Messages.SettlementMissionMessage;
import static org.galatea.starter.entrypoint.messagecontracts.Messages.SettlementResponseMessage;
import static org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessage;
import static org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessages;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
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

    SettlementResponseMessage response = fuseServer.sendTradeAgreement(
        TradeAgreementMessages.newBuilder().addMessage(
            TradeAgreementMessage.newBuilder().setId(4000L)
                .setInstrument("IBM").setInternalParty("icp-1")
                .setExternalParty("ecp-1").setBuySell("B").setQty(4500).build()
        ).addMessage(
            TradeAgreementMessage.newBuilder().setId(40001L)
                .setInstrument("IBM").setInternalParty("icp-2")
                .setExternalParty("ecp-2").setBuySell("B").setQty(4600).build()
        ).build()
    );

    List<String> missionPaths = new ArrayList<>(response.getSpawnedMissionPathsList());

    log.info("created missions: {}", missionPaths);

    SettlementMissionMessage.Builder b1 = SettlementMissionMessage.newBuilder().setDepot("DTC")
        .setInstrument("IBM").setExternalParty("ecp-1").setDirection("REC").setQty(4500);

    SettlementMissionMessage.Builder b2 = SettlementMissionMessage.newBuilder().setDepot("DTC")
        .setInstrument("IBM").setExternalParty("ecp-2").setDirection("REC").setQty(4600);

    assertEquals(2, missionPaths.size());

    for (String missionPath : missionPaths) {
      Long missionId = Long.parseLong(missionPath.split("/")[3]); // brittle assumption...
      SettlementMissionMessage settlementMission = fuseServer.getSettlementMission(missionId);
      log.info("fetched mission: {}", settlementMission);

      assertTrue(b1.setId(missionId).build().equals(settlementMission)
          || b2.setId(missionId).build().equals(settlementMission));
    }
  }
}
