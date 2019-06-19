package org.galatea.starter.entrypoint;

import java.util.Arrays;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessage;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class SettlementJmsListener {

  @NonNull
  protected SettlementService settlementService;

  @NonNull
  protected ITranslator<byte[], TradeAgreement> tradeAgreementProtoTranslator;

  @NonNull
  protected ITranslator<TradeAgreementMessage, TradeAgreement> tradeAgreementMessageTranslator;

  /**
   * Spawns Missions for any TradeAgreements pulled off the jms queue in JSON format.
   */
  @JmsListener(destination = "${jms.agreement-queue-json}",
      concurrency = "${jms.listener-concurrency}")
  public void settleAgreementJson(final TradeAgreementMessage agreementMessage) {
    log.info("Handling agreements {}", agreementMessage);

    TradeAgreement agreement = tradeAgreementMessageTranslator.translate(agreementMessage);
    Set<Long> missionIds = settlementService.spawnMissions(Arrays.asList(agreement));
    log.info("Created missions {}", missionIds);
  }

  /**
   * Spawns missions for any TradeAgreements pulled off the jms queue in protobuf format.
   */
  @JmsListener(destination = "${jms.agreement-queue-proto}",
      concurrency = "${jms.listener-concurrency}")
  public void settleAgreementProto(final byte[] message) {
    log.info("Received message. Translating.");
    TradeAgreement agreement = tradeAgreementProtoTranslator.translate(message);

    log.info("Handling agreement {}", agreement);

    Set<Long> missionIds = settlementService.spawnMissions(Arrays.asList(agreement));
    log.info("Created missions {}", missionIds);
  }
}
