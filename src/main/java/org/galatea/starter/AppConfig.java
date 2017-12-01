package org.galatea.starter;

import lombok.extern.slf4j.Slf4j;

import net.sf.aspect4log.aspect.LogAspect;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.service.AgreementTransformer;
import org.galatea.starter.service.IAgreementTransformer;
import org.galatea.starter.service.ProceedsCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

    @Bean
    public LogAspect createLogAspect() {
    return new LogAspect();
  }

}
