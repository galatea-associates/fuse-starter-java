package org.galatea.starter.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.domain.rpsy.ISettlementMissionRpsy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@Log
@Validated
@Service
public class SettlementService {

  @NonNull
  ISettlementMissionRpsy missionrpsy;

  @NonNull
  IAgreementTransformer agreementTransformer;

  /**
   * Create missions based on the agreements provided.
   *
   * @param agreements the agreements used to generate missions
   * @return the ids of the missions that were created
   */
  public Set<Long> spawnMissions(@Valid final List<TradeAgreement> agreements) {

    // Map each agreement to a mission, collect to a list, and then same in bulk
    Iterable<SettlementMission> savedMissions = missionrpsy.save(agreements.stream()
        .map(agr -> agreementTransformer.transform(agr)).collect(Collectors.toList()));
    log.debug("The following missions were saved: {}", savedMissions);

    // We have to do all of this StreamSupport crap since the repository returns an iterable instead
    // of a normal collection
    Set<Long> idSet = StreamSupport.stream(savedMissions.spliterator(), false)
        .map(SettlementMission::getId).collect(Collectors.toSet());
    log.info("Returning {} mission id(s)", idSet.size());

    return idSet;
  }

  public Optional<SettlementMission> findMission(final Long id) {
    return Optional.ofNullable(missionrpsy.findOne(id));
  }

  /**
   * Update the mission with the ID using the given TradeAgreement.
   *
   * @param id identifier of the mission
   * @param agreement the agreement used to update the mission to
   * @return optional containing the saved mission
   */
  public Optional<SettlementMission> updateMission(final Long id, final TradeAgreement agreement) {
    SettlementMission settlementMission = agreementTransformer.transform(agreement);
    settlementMission.setId(id);
    SettlementMission savedMission = missionrpsy.save(settlementMission);
    log.info("The following mission was updated: {}", savedMission);
    return Optional.ofNullable(savedMission);
  }

  /**
   * @param id identifier of the mission
   * @return does a mission with the id exist?
   */
  public boolean missionExists(final Long id) {
    return missionrpsy.exists(id);
  }

  /**
   * Delete the mission by ID.
   * This removes the mission from the cache as well.
   *
   * @param id identifier of the mission to delete
   */
  public void deleteMission(final Long id) {
    missionrpsy.delete(id);
    log.info("Mission with id '{}' was deleted", id);
  }
}
