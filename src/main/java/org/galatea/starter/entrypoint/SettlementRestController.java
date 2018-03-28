package org.galatea.starter.entrypoint;

import static java.util.Collections.singletonList;
import static org.galatea.starter.entrypoint.messagecontracts.Messages.SettlementMissionMessage;
import static org.galatea.starter.entrypoint.messagecontracts.Messages.TradeAgreementMessage;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.exception.EntityNotFoundException;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.Tracer;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST Controller that generates and listens to http endpoints which allow the caller to create
 * Missions from TradeAgreements and query them back out.
 */
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@Validated
@RestController
public class SettlementRestController {

  @NonNull
  SettlementService settlementService;

  @NonNull
  ITranslator<SettlementMission, SettlementMissionMessage> settlementMissionTranslator;

  @NonNull
  ITranslator<TradeAgreementMessage, TradeAgreement> tradeAgreementTranslator;

  public static final String SETTLE_MISSION_PATH = "/settlementEngine";
  public static final String GET_MISSION_PATH = SETTLE_MISSION_PATH + "/mission/";

  private static final String APPLICATION_X_PROTOBUF = "application/x-protobuf";

  /**
   * Generate Missions from a provided TradeAgreement.
   */
  // @PostMapping to link http POST requests to this method
  // @RequestBody to have the post request body deserialized into a list of TradeAgreement objects
  @PostMapping(value = SETTLE_MISSION_PATH, consumes = { APPLICATION_X_PROTOBUF, MediaType.APPLICATION_JSON_VALUE})
  public Set<String> settleAgreement(
      @RequestBody final TradeAgreementMessage message,
      @RequestParam(value = "requestId", required = false) String requestId) {

    // if an external request id was provided, grab it
    processRequestId(requestId);

    TradeAgreement agreement = tradeAgreementTranslator.translate(message);

    Set<Long> missionIds = settlementService.spawnMissions(singletonList(agreement));

    return missionIds.stream().map(id -> GET_MISSION_PATH + id).collect(Collectors.toSet());
  }

  /**
   * Retrieve a previously generated Mission.
   */
  // @GetMapping to link http GET requests to this method
  // @PathVariable to take the id from the path and make it available as a method argument
  // @RequestParam to take a parameter from the url (ex: http://url?requestId=3123)
  @GetMapping(value = GET_MISSION_PATH + "{id}", produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE, APPLICATION_X_PROTOBUF})
  public @ResponseBody SettlementMissionMessage getMission(@PathVariable final Long id,
      @RequestParam(value = "requestId", required = false) String requestId) {

    // if an external request id was provided, grab it
    processRequestId(requestId);

    Optional<SettlementMission> msn = settlementService.findMission(id);

    if (msn.isPresent()) {
      return settlementMissionTranslator.translate(msn.get());
    }

    throw new EntityNotFoundException(SettlementMission.class, id.toString());
  }

  /**
   * Adds the specified requestId to the context for this request (if not null).
   */
  private void processRequestId(String requestId) {
    if (requestId != null) {
      log.info("Request received with id: {}", requestId);
      Tracer.setExternalRequestId(requestId);
    }
  }

}
