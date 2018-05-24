package org.galatea.starter.entrypoint;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.service.SettlementService;

/**
 * Implements a base class for settlement rest controllers to avoid duplicating
 * the logic of calling the settlement service.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
public abstract class BaseSettlementRestController extends BaseRestController {

  @NonNull
  SettlementService settlementService;

  /**
   * Invokes the settlement service to spawn missions for the specified trade agreements.
   */
  protected Set<String> settleAgreementInternal(List<TradeAgreement> agreements, String getMissionPath) {

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
}
