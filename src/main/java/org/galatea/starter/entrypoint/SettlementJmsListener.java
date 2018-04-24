
package org.galatea.starter.entrypoint;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Component
public class SettlementJmsListener {

  @NonNull
  protected SettlementService settlementService;

  @NonNull
  protected ITranslator<byte[], TradeAgreement> tradeAgreementTranslator;

  /**
   * Spawns Missions for any TradeAgreements pulled off the jms queue.
   */
  @JmsListener(destination = "${jms.agreement-queue}", concurrency = "${jms.listener-concurrency}")
  public void settleAgreement(final byte[] message) {
    log.info("Received message. Translating.");
    TradeAgreement agreement = tradeAgreementTranslator.translate(message);

    log.info("Handling agreement {}", agreement);

    Set<Long> missionIds = settlementService.spawnMissions(Arrays.asList(agreement));
    log.info("Created missions {}", missionIds);
  }

}
