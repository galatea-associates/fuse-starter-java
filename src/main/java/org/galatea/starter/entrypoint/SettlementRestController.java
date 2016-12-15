package org.galatea.starter.entrypoint;

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

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@RestController
public class SettlementRestController {

  @NonNull
  SettlementService settlementService;

  public static final String SETTLE_MISSION_PATH = "/settlementEngine";
  public static final String GET_MISSION_PATH = SETTLE_MISSION_PATH + "/mission/";

  @PostMapping(value = SETTLE_MISSION_PATH, consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Set<String>> settleAgreement(
      @RequestBody final List<TradeAgreement> agreements) {
    Set<Long> missionIds = settlementService.spawnMissions(agreements);
    Set<String> missionIdUris =
        missionIds.stream().map(id -> GET_MISSION_PATH + id).collect(Collectors.toSet());

    return ResponseEntity.accepted().body(missionIdUris);
  }

  @GetMapping(value = GET_MISSION_PATH + "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SettlementMission> getMission(@PathVariable final Long id) {

    Optional<SettlementMission> msn = settlementService.findMission(id);

    if (msn.isPresent()) {
      return ResponseEntity.ok(msn.get());
    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }



}
