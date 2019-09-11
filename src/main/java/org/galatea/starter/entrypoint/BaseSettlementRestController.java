package org.galatea.starter.entrypoint;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.service.SettlementService;

/**
 * Implements a base class for settlement rest controllers to avoid duplicating the logic of calling
 * the settlement service.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
public abstract class BaseSettlementRestController extends BaseRestController {

  @NonNull
  SettlementService settlementService;

  /**
   * Invokes the settlement service to spawn missions for the specified trade agreements.
   */
  protected Set<String> settleAgreementInternal(final List<TradeAgreement> agreements,
      final String getMissionPath) {

    Set<Long> missionIds = settlementService.spawnMissions(agreements);
    return missionIds.stream().map(id -> getMissionPath + id)
        .collect(Collectors.toSet());
  }

  /**
   * Retrieves settlement missions from the settlement service.
   */
  protected Optional<SettlementMission> getMissionInternal(final Long id) {
    return settlementService.findMission(id);
  }

  /**
   * Retrieves multiple settlement missions from the settlement service.
   *
   * @param ids a comma-separated list of IDs of the missions to retrieve
   */
  protected List<SettlementMission> getMissionsInternal(final List<Long> ids) {
    return settlementService.findMissions(ids);
  }

  /**
   * Updates settlement mission, if it exists.
   */
  protected Optional<SettlementMission> updateMissionInternal(final Long id,
      final SettlementMission mission) {
    if (settlementService.missionExists(id)) {
      return settlementService.updateMission(id, mission);
    } else {
      return Optional.empty();
    }
  }

  /**
   * Deletes a settlement mission from the settlement service.
   */
  protected void deleteMissionInternal(final Long id) {
    settlementService.deleteMission(id);
  }
}
