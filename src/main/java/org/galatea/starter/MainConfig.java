package org.galatea.starter;

import net.sf.aspect4log.aspect.LogAspect;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.rpsy.ISettlementMissionRpsy;
import org.galatea.starter.service.IAgreementTransformer;
import org.galatea.starter.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class MainConfig {

  @Autowired
  ISettlementMissionRpsy missionrpsy;

  @Bean
  public LogAspect createLogAspect() {
    return new LogAspect();
  }

  @Bean
  public SettlementService settlementService() {
    return new SettlementService(missionrpsy, agreementTransformer());
  }

  @Bean
  public IAgreementTransformer agreementTransformer() {
    return agreement -> SettlementMission.builder().instrument(agreement.getInstrument())
        .externalParty(agreement.getExternalParty()).depot("DTC").qty(agreement.getQty())
        .direction("B".equals(agreement.getBuySell()) ? "REC" : "DEL").build();

  }
}
