package org.galatea.starter.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.domain.rpsy.ISettlementMissionRpsy;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class SettlementServiceTest extends ASpringTest {
  @MockBean
  private ISettlementMissionRpsy mockSettlementMissionRpsy;

  @MockBean
  private IAgreementTransformer mockAgreementTransformer;

  @Test
  public void testFindMissionFound() {
    Long id = 1L;

    SettlementMission testSettlementMission = SettlementMission.builder().id(id).depot("DTC")
        .externalParty("EXT-1").instrument("IBM").direction("REC").qty(100d).build();

    given(this.mockSettlementMissionRpsy.findOne(id)).willReturn(testSettlementMission);

    SettlementService service =
        new SettlementService(this.mockSettlementMissionRpsy, this.mockAgreementTransformer);

    Optional<SettlementMission> maybeRetrieved = service.findMission(id);
    assertTrue(maybeRetrieved.isPresent());
  }

  @Test
  public void testFindMissionNotFound() {
    Long id = 1L;

    SettlementMission testSettlementMission = SettlementMission.builder().id(id).depot("DTC")
        .externalParty("EXT-1").instrument("IBM").direction("REC").qty(100d).build();

    given(this.mockSettlementMissionRpsy.findOne(id)).willReturn(testSettlementMission);

    SettlementService service =
        new SettlementService(this.mockSettlementMissionRpsy, this.mockAgreementTransformer);

    Optional<SettlementMission> maybeRetrieved = service.findMission(id + 1); // not the same id!!!
    assertFalse(maybeRetrieved.isPresent());
  }

  @Test
  public void testSpawnMissions() {

    SettlementMission testSettlementMission = SettlementMission.builder().id(35L).depot("DTC")
        .externalParty("EXT-1").instrument("IBM").direction("REC").qty(100d).build();

    TradeAgreement testTradeAgreement = TradeAgreement.builder().id(45L).instrument("instr-1")
        .internalParty("icp-1").externalParty("ecp-1").buySell("B").qty(4500.0).build();

    given(this.mockSettlementMissionRpsy.save(Mockito.anyList()))
        .willReturn(Collections.singletonList(testSettlementMission));

    SettlementService service =
        new SettlementService(this.mockSettlementMissionRpsy, this.mockAgreementTransformer);

    Set<Long> missionIds = service.spawnMissions(Collections.singletonList(testTradeAgreement));
    assertEquals(1, missionIds.size());
  }
}
