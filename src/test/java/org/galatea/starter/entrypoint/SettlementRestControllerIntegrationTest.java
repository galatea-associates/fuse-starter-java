
package org.galatea.starter.entrypoint;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.SettlementMission.SettlementMissionBuilder;
import org.galatea.starter.domain.TradeAgreement;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;


@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class SettlementRestControllerIntegrationTest {

  interface FuseServer {
    @RequestLine("POST /settlementEngine")
    @Headers("Content-Type: application/json")
    List<String> sendTradeAgreement(TradeAgreement[] tradeAgreements);

    @RequestLine("GET /settlementEngine/mission/{id}")
    SettlementMission getSettlementMission(@Param("id") Long id);
  }

  @Test
  public void testMissionCreation() {
    String fuseHostName = System.getProperty("fuse.sandbox.url");
    if (fuseHostName == null || fuseHostName.isEmpty()) {
      // TODO: the base URL should probably be moved to a src/test/resources properties file
      fuseHostName = "http://fuse-rest-dev.cfapps.io";
    }

    FuseServer fuseServer = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder())
        .target(FuseServer.class, fuseHostName);

    List<String> missionPaths = fuseServer.sendTradeAgreement(new TradeAgreement[] {

        TradeAgreement.builder().id(4000L).instrument("IBM").internalParty("icp-1")
            .externalParty("ecp-1").buySell("B").qty(4500.0).build(),

        TradeAgreement.builder().id(4001L).instrument("IBM").internalParty("icp-2")
            .externalParty("ecp-2").buySell("B").qty(4600.0).build()});

    log.info("created missions: {}", missionPaths);

    SettlementMissionBuilder b1 = SettlementMission.builder().depot("DTC").instrument("IBM")
        .externalParty("ecp-1").direction("REC").qty(4500.0);
    SettlementMissionBuilder b2 = SettlementMission.builder().depot("DTC").instrument("IBM")
        .externalParty("ecp-2").direction("REC").qty(4600.0);

    assertEquals(2, missionPaths.size());

    for (String missionPath : missionPaths) {
      Long missionId = Long.parseLong(missionPath.split("/")[3]); // brittle assumption...
      SettlementMission settlementMission = fuseServer.getSettlementMission(missionId);
      log.info("fetched mission: {}", settlementMission);

      assertTrue(b1.id(missionId).build().equals(settlementMission)
          || b2.id(missionId).build().equals(settlementMission));
    }
  }
}
