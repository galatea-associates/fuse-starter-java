package org.galatea.starter;

import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.aspect.LogAspect;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.service.IAgreementTransformer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@Configuration
@EnableAspectJAutoProxy
@EnableCaching // pom imports net.sf.ehcache:ehcache, src/main/resources/ehcache.xml configures.
public class AppConfig {

  @Bean
  public LogAspect createLogAspect() {
    return new LogAspect();
  }

  @Bean
  public IAgreementTransformer agreementTransformer() {
    return agreement -> SettlementMission.builder().instrument(agreement.getInstrument())
        .externalParty(agreement.getExternalParty()).depot("DTC").qty(agreement.getQty())
        .direction("B".equals(agreement.getBuySell()) ? "REC" : "DEL").build();

  }
}
