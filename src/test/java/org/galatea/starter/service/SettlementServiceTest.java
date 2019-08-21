package org.galatea.starter.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.domain.rpsy.ISettlementMissionRpsy;
import org.galatea.starter.entrypoint.exception.EntityNotFoundException;
import org.galatea.starter.testutils.TestDataGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

public class SettlementServiceTest extends ASpringTest {

  @MockBean
  private ISettlementMissionRpsy mockSettlementMissionRpsy;

  @MockBean
  private IAgreementTransformer mockAgreementTransformer;

  private SettlementService service;

  @Before
  public void setup() {
    service = new SettlementService(mockSettlementMissionRpsy, mockAgreementTransformer);
  }

  @Test
  public void testFindMissionFound() {
    Long id = 1L;

    SettlementMission testSettlementMission
        = TestDataGenerator.defaultSettlementMissionData().build();

    given(this.mockSettlementMissionRpsy.findById(id)).willReturn(Optional.of(testSettlementMission));

    Optional<SettlementMission> maybeRetrieved = service.findMission(id);
    assertTrue(maybeRetrieved.isPresent());
  }

  @Test
  public void testFindMissionNotFound() {
    Long id = 1L;

    SettlementMission testSettlementMission
        = TestDataGenerator.defaultSettlementMissionData().id(id).build();

    given(this.mockSettlementMissionRpsy.findById(id)).willReturn(Optional.of(testSettlementMission));

    Optional<SettlementMission> maybeRetrieved = service.findMission(id + 1); // not the same id!!!
    assertFalse(maybeRetrieved.isPresent());
  }

  @Test
  public void testFindMissionsFound() {
    List<Long> ids = Arrays.asList(1L, 2L);
    SettlementMission settlementMission1 = TestDataGenerator.defaultSettlementMissionData()
        .id(1L).build();
    SettlementMission settlementMission2 = TestDataGenerator.defaultSettlementMissionData()
        .id(2L).build();
    List<SettlementMission> settlementMissions = Arrays.asList(
        settlementMission1, settlementMission2);

    given(this.mockSettlementMissionRpsy.findAllById(ids)).willReturn(settlementMissions);

    List<SettlementMission> actual = service.findMissions(ids);
    assertEquals(settlementMissions, actual);
  }

  @Test
  public void testFindMissionsNotFound() {
    List<Long> ids = Arrays.asList(1L, 2L);
    SettlementMission settlementMission1 = SettlementMission.builder()
        .id(1L).depot("DTC").externalParty("EXT-1").instrument("IBM").direction("REC").qty(100d)
        .version(0L).build();
    List<SettlementMission> settlementMissions = Collections.singletonList(settlementMission1);

    given(this.mockSettlementMissionRpsy.findAllById(ids)).willReturn(settlementMissions);

    try {
      service.findMissions(ids);
      fail("An EntityNotFoundException was expected but not thrown");
    } catch (EntityNotFoundException e) {
      // Exception is expected
    }
  }

  @Test
  public void testSpawnMissions() {

    SettlementMission testSettlementMission = SettlementMission.builder().id(35L).depot("DTC")
        .externalParty("EXT-1").instrument("IBM").direction("REC").qty(100d).version(0L).build();

    TradeAgreement testTradeAgreement = TradeAgreement.builder().instrument("instr-1")
        .internalParty("icp-1").externalParty("ecp-1").buySell("B").qty(4500.0).build();

    given(this.mockSettlementMissionRpsy.saveAll(Mockito.anyList()))
        .willReturn(Collections.singletonList(testSettlementMission));

    Set<Long> missionIds = service.spawnMissions(Collections.singletonList(testTradeAgreement));
    assertEquals(1, missionIds.size());
  }

  @Test
  public void testUpdateMission() {

    SettlementMission testSettlementMission = SettlementMission.builder().depot("DTC")
        .externalParty("EXT-1").instrument("IBM").direction("REC").qty(100d).version(0L).build();

    given(this.mockSettlementMissionRpsy.save(testSettlementMission))
        .willReturn(testSettlementMission);

    SettlementService service =
        new SettlementService(this.mockSettlementMissionRpsy, this.mockAgreementTransformer);

    Optional<SettlementMission> settlementMissionOptional = service.updateMission(35L, testSettlementMission);
    assertEquals((Long) 35L, settlementMissionOptional.get().getId());
  }

  @Test
  public void testMissionExists() {

    given(this.mockSettlementMissionRpsy.exists(35L))
        .willReturn(true);

    SettlementService service =
        new SettlementService(this.mockSettlementMissionRpsy, this.mockAgreementTransformer);

    boolean missionExists = service.missionExists(35L);
    assertTrue(missionExists);
  }

  @Test
  public void testDeleteMission() {

    doNothing().when(this.mockSettlementMissionRpsy).delete(35L);

    SettlementService service =
        new SettlementService(this.mockSettlementMissionRpsy, this.mockAgreementTransformer);

    service.deleteMission(35L);
    boolean missionExists = service.missionExists(35L);
    assertFalse(missionExists);
  }
}
