package org.galatea.starter;

import feign.Logger;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.aspect.LogAspect;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@Configuration
@EnableAspectJAutoProxy
@EnableCaching
@EnableFeignClients
public class AppConfig {

  /**
   * Create a LogAspect for use with the SpringAOP @Log annotation.
   */
  @Bean
  public LogAspect createLogAspect() {
    return new LogAspect();
  }

  /**
   * Returns an anonymous class implementing the IAgreementTransformer interface. Demonstrates the
   * use of a lambda function which can stand in as an anonymous class with a single method:
   * https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
   */
  @Bean
  public IAgreementTransformer agreementTransformer() {
    return agreement -> SettlementMission.builder().instrument(agreement.getInstrument())
        .externalParty(agreement.getExternalParty()).depot("DTC").qty(agreement.getQty())
        .direction("B".equals(agreement.getBuySell()) ? "REC" : "DEL").version(0L).build();
  }

  /**
   * Set the Feign log level for interfaces annotated with @FeignClient.
   *
   * @return the Feign log level.
   */
  @Bean
  public Logger.Level logLevel() {
    return Logger.Level.BASIC;
  }

}
