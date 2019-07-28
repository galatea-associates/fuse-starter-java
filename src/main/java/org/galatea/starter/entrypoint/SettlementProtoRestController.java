package org.galatea.starter.entrypoint;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementMissionProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementResponseProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessages;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that mimics the behavior of SettlementRestController but accepts and returns only
 * protobuf messages.
 */
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@Validated
@RestController
public class SettlementProtoRestController extends BaseSettlementRestController {

  private static final String APPLICATION_X_PROTOBUF = "application/x-protobuf";

  @NonNull
  private ITranslator<SettlementMission, SettlementMissionProtoMessage> settlementMissionTranslator;

  @NonNull
  private ITranslator<TradeAgreementProtoMessages, List<TradeAgreement>> tradeAgreementTranslator;

  @Value("${mvc.settleMissionPath}")
  private String settleMissionPath;

  @Value("${mvc.getMissionPath}")
  private String getMissionPath;

  /**
   * Initializes a new instance of this class with the required arguments that will be autowired by
   * spring boot. This constructor was manually added because of the base class that has no default
   * constructor, necessitating a call to super() from here.
   */
  public SettlementProtoRestController(final SettlementService settlementService,
      final ITranslator<TradeAgreementProtoMessages, List<TradeAgreement>> tradeAgreementTranslator,
      final ITranslator<SettlementMission, SettlementMissionProtoMessage>
          settlementMissionTranslator) {
    super(settlementService);
    this.settlementMissionTranslator = settlementMissionTranslator;
    this.tradeAgreementTranslator = tradeAgreementTranslator;
  }

  /**
   * Spawn settlement missions from the supplied trade agreement messages.
   */
  @PostMapping(value = "${mvc.settleMissionPath}", consumes = APPLICATION_X_PROTOBUF,
      produces = APPLICATION_X_PROTOBUF)
  public SettlementResponseProtoMessage settleAgreement(
      @RequestBody final TradeAgreementProtoMessages messages,
      @RequestParam(value = "requestId", required = false) final String requestId) {
    // if an external request id was provided, grab it
    processRequestId(requestId);

    List<TradeAgreement> agreements = tradeAgreementTranslator.translate(messages);
    Set<String> missionPaths = settleAgreementInternal(agreements, getMissionPath);

    return SettlementResponseProtoMessage.newBuilder().addAllSpawnedMissionPaths(missionPaths)
        .build();
  }

  /**
   * Retrieves existing settlement mission messages.
   */
  @GetMapping(value = "${mvc.getMissionPath}" + "{id}", produces = APPLICATION_X_PROTOBUF)
  public ResponseEntity<SettlementMissionProtoMessage> getMission(@PathVariable final Long id,
      @RequestParam(value = "requestId", required = false) final String requestId) {
    // if an external request id was provided, grab it
    processRequestId(requestId);

    Optional<SettlementMission> msn = getMissionInternal(id);

    if (msn.isPresent()) {
      return new ResponseEntity<>(settlementMissionTranslator.translate(msn.get()), HttpStatus.OK);
    }

    // An HTTP request that gets here will be expecting a protobuf response body, so we can't throw
    // an exception here without some way to convert the exception to protobuf
    // See issue #272
    // Originally an EntityNotFoundException was thrown here, but it was changed to this so that we
    // could add a test for the not-found case in order to meet coverage requirements
    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
  }
}
