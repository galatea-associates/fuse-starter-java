package org.galatea.starter.entrypoint;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.MvcConfig;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.exception.EntityNotFoundException;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionList;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionMessage;
import org.galatea.starter.entrypoint.messagecontracts.SettlementResponseMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessages;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller that generates and listens to http endpoints which allow the caller to create
 * Missions from TradeAgreements and query them back out.
 */
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@Validated
@RestController
public class SettlementRestController extends BaseSettlementRestController {

  @NonNull
  ITranslator<SettlementMission, SettlementMissionMessage> settlementMissionTranslator;

  @NonNull
  ITranslator<SettlementMissionMessage, SettlementMission> settlementMissionMsgTranslator;

  @NonNull
  ITranslator<TradeAgreementMessages, List<TradeAgreement>> tradeAgreementTranslator;

  @Value("${mvc.settleMissionPath}")
  private String settleMissionPath;

  @Value("${mvc.getMissionPath}")
  private String getMissionPath;

  /**
   * Initializes a new instance of this class with the required arguments that will be autowired by
   * spring boot. This constructor was manually added because of the base class that has no default
   * constructor, necessitating a call to super() from here.
   */
  public SettlementRestController(final SettlementService settlementService,
      final ITranslator<TradeAgreementMessages, List<TradeAgreement>> tradeAgreementTranslator,
      final ITranslator<SettlementMission, SettlementMissionMessage> settlementMissionTranslator,
      final ITranslator<SettlementMissionMessage, SettlementMission>
          settlementMissionMsgTranslator) {
    super(settlementService);
    this.tradeAgreementTranslator = tradeAgreementTranslator;
    this.settlementMissionTranslator = settlementMissionTranslator;
    this.settlementMissionMsgTranslator = settlementMissionMsgTranslator;
  }

  /**
   * Generate Missions from a provided TradeAgreement.
   */
  // @PostMapping to link http POST requests to this method
  // @RequestBody to have the post request body deserialized into a list of TradeAgreement objects
  @PostMapping(value = "${mvc.settleMissionPath}", consumes = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public SettlementResponseMessage settleAgreement(
      @RequestBody final TradeAgreementMessages messages,
      @RequestParam(value = "requestId", required = false) final String requestId) {

    // if an external request id was provided, grab it
    processRequestId(requestId);

    List<TradeAgreement> agreements = tradeAgreementTranslator.translate(messages);

    Set<String> missionPaths = settleAgreementInternal(agreements, getMissionPath);

    return SettlementResponseMessage.builder().spawnedMissions(missionPaths).build();
  }

  /**
   * Retrieve a previously generated Mission.
   */
  // @GetMapping to link http GET requests to this method
  // @PathVariable to take the id from the path and make it available as a method argument
  // @RequestParam to take a parameter from the url (ex: http://url?requestId=3123)
  @GetMapping(value = "${mvc.getMissionPath}" + "{id}", produces = {
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public SettlementMissionMessage getMission(@PathVariable final Long id,
      @RequestParam(value = "requestId", required = false) final String requestId) {

    // if an external request id was provided, grab it
    processRequestId(requestId);

    Optional<SettlementMission> msn = getMissionInternal(id);

    if (msn.isPresent()) {
      return settlementMissionTranslator.translate(msn.get());
    }

    throw new EntityNotFoundException(SettlementMission.class, id.toString());
  }

  /**
   * Retrieve multiple previously generated Missions.
   */
  // @GetMapping to link http GET requests to this method
  @GetMapping(value = "${mvc.getMissionsPath}", produces = {
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE,
      MvcConfig.TEXT_CSV_VALUE,
      MvcConfig.APPLICATION_EXCEL_VALUE})
  public SettlementMissionList getMissions(
      // @RequestParam to take a comma-separated list of ids from the url (ex: http://url?ids=1,2,3)
      @RequestParam(value = "ids") final String ids,
      // @RequestParam to take a parameter from the url (ex: http://url?requestId=3123)
      @RequestParam(value = "requestId", required = false) final String requestId) {

    // if an external request id was provided, grab it
    processRequestId(requestId);

    List<Long> idLongs = Arrays.stream(ids.split(","))
        .map(Long::parseLong)
        .collect(Collectors.toList());
    List<SettlementMission> missions = getMissionsInternal(idLongs);

    return new SettlementMissionList(missions);
  }

  /**
   * Update an existing mission given an ID.
   */
  // @PutMapping to link http PUT requests to this method
  // @PathVariable to take the id from the path and make it available as a method argument
  // @RequestParam to take a parameter from the url (ex: http://url?requestId=3123)
  @PutMapping(value = "${mvc.updateMissionPath}" + "{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public void updateMission(@PathVariable final Long id,
      @RequestBody final SettlementMissionMessage settlementMissionMessage,
      @RequestParam(value = "requestId", required = false) final String requestId) {

    // if an external request id was provided, grab it
    processRequestId(requestId);

    // Translate the message and get it back from the list
    SettlementMission settlementMission =
        settlementMissionMsgTranslator.translate(settlementMissionMessage);

    Optional<SettlementMission> msn = updateMissionInternal(id, settlementMission);

    if (!msn.isPresent()) {
      // The mission was not found and could not be updated
      throw new EntityNotFoundException(SettlementMission.class, id.toString());
    }
  }

  /**
   * Delete a previously created mission.
   */
  // @DeleteMapping to link http DELETE requests to this method
  // @PathVariable to take the id from the path and make it available as a method argument
  // @RequestParam to take a parameter from the url (ex: http://url?requestId=3123)
  @DeleteMapping(value = "${mvc.deleteMissionPath}" + "{id}", produces = {
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public void deleteMission(@PathVariable final Long id,
      @RequestParam(value = "requestId", required = false) final String requestId) {

    // if an external request id was provided, grab it
    processRequestId(requestId);

    try {
      deleteMissionInternal(id);
    } catch (EmptyResultDataAccessException e) {
      // The entity could not be deleted because it does not exist
      throw new EntityNotFoundException(SettlementMission.class, id.toString(), e);
    }
  }

}
