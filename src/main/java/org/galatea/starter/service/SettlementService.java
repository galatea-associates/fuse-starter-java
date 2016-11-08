package org.galatea.starter.service;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.domain.rpsy.ISettlementMissionRpsy;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log
@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
public class SettlementService {

  @NonNull
  ISettlementMissionRpsy missionrpsy;

  @NonNull
  IAgreementTransformer agreementTransformer;

  public Set<Long> spawnMissions(final List<TradeAgreement> agreements) {

    // Map each agreement to a mission, collect to a list, and then same in bulk
    Iterable<SettlementMission> savedMissions = missionrpsy.save(agreements.stream()
        .map(agr -> agreementTransformer.transform(agr)).collect(Collectors.toList()));
    log.debug("The following missions were saved: {}", savedMissions);

    // We have to do all of this StreamSupport crap since the repository returns an iterable instead
    // of a normal collection
    Set<Long> idSet = StreamSupport.stream(savedMissions.spliterator(), false)
        .map(msn -> msn.getId()).collect(Collectors.toSet());
    log.info("Returning {} mission id(s)", idSet.size());

    return idSet;
  }

  public Optional<SettlementMission> findMission(final Long id) {
    return Optional.ofNullable(missionrpsy.findOne(id));
  }

}
