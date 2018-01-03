package org.galatea.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
@EnableCaching
public class CachingConfig extends CachingConfigurerSupport {

  public static final String EHCACHE_CONFIG = "ehcache.xml";

  @Bean
  @Override
  public CacheManager cacheManager() {
    EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
    ehCacheCacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
    return ehCacheCacheManager;
  }

  /** Configures bean for EhCacheManager. */
  @Bean
  public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
    EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
    ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource(EHCACHE_CONFIG));
    ehCacheManagerFactoryBean.setShared(true);
    return ehCacheManagerFactoryBean;
  }
}
