package org.galatea.starter.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.domain.rpsy.ISettlementMissionRpsy;
import org.galatea.starter.entrypoint.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
    Iterable<SettlementMission> savedMissions = missionrpsy.saveAll(agreements.stream()
        .map(agr -> agreementTransformer.transform(agr)).collect(Collectors.toList()));
    log.debug("The following missions were saved: {}", savedMissions);

    // We have to do all of this StreamSupport crap since the repository returns an iterable instead
    // of a normal collection
    Set<Long> idSet = StreamSupport.stream(savedMissions.spliterator(), false)
        .map(SettlementMission::getId).collect(Collectors.toSet());
    log.info("Returning {} mission id(s)", idSet.size());

    return idSet;
  }


  /**
   * Retrieve a previously-generated settlement mission from the database.
   *
   * @param id the ID of the mission to retrieve
   */
  public Optional<SettlementMission> findMission(final Long id) {
    log.info("Retrieving settlement mission with id {}", id);
    return missionrpsy.findById(id);
  }

  /**
   * Retrieve multiple previously-generated settlement missions from the database.
   *
   * @param ids a comma-separated list of IDs of the missions to retrieve
   */
  public List<SettlementMission> findMissions(final List<Long> ids) {
    log.info("Retrieving settlement missions with ids: {}", ids);

    List<SettlementMission> retrievedMissions = Lists.newArrayList(missionrpsy.findAllById(ids));

    // CrudRepository.findAll(Iterable ids) succeeds even if some provided IDs aren't found, so
    // if we want to alert on any not-found IDs we have to manually check
    Set<Long> retrievedMissionIds = retrievedMissions.stream()
        .map(SettlementMission::getId)
        .collect(Collectors.toSet());
    Sets.SetView<Long> missingMissions = Sets.difference(new HashSet<>(ids), retrievedMissionIds);
    if (!missingMissions.isEmpty()) {
      throw new EntityNotFoundException(SettlementMission.class, missingMissions);
    }

    return retrievedMissions;
  }
}
