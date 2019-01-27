package org.galatea.starter;

import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.aspect.LogAspect;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.service.IAgreementTransformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
@EnableAspectJAutoProxy
@EnableCaching
public class AppConfig {

  @Value("${cache-config}")
  public String cacheConfig;

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
        .direction("B".equals(agreement.getBuySell()) ? "REC" : "DEL").build();
  }

  /**
   * CacheManager that contains the Cache Configuration. Spring will use this cache if any
   * cache annotations are used.
   */
  @Bean
  public CacheManager cacheManager() {
    return new EhCacheCacheManager(ehCacheCacheManager().getObject());
  }

  @Bean
  public EhCacheManagerFactoryBean ehCacheCacheManager() {
    EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
    cmfb.setConfigLocation(new ClassPathResource(cacheConfig));
    cmfb.setShared(true);
    return cmfb;
  }
}
