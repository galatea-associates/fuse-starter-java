package org.galatea.starter.entrypoint;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring
@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
public class SettlementRestController {

  @NonNull
  @Autowired
  SettlementService settlementService;

  @PostMapping(value = "/settlementEngine", consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Set<String>> settleAgreement(
      @RequestBody final List<TradeAgreement> agreements) {
    Set<Long> missionIds = settlementService.spawnMissions(agreements);
    Set<String> missionIdUris = missionIds.stream().map(id -> "/settlementEngine/mission/" + id)
        .collect(Collectors.toSet());

    return ResponseEntity.accepted().body(missionIdUris);
  }

  @GetMapping(value = "/settlementEngine/mission/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SettlementMission> findMission(@PathVariable final Long id) {

    Optional<SettlementMission> msn = settlementService.findMission(id);

    if (msn.isPresent()) {
      return ResponseEntity.ok(msn.get());
    }

    return new ResponseEntity<SettlementMission>(HttpStatus.NOT_FOUND);
  }



}
