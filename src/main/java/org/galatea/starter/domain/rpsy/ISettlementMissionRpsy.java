package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.SettlementMission;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ISettlementMissionRpsy extends CrudRepository<SettlementMission, Long> {
  
  List<SettlementMission> findByDepot(String depot);

  @Override
  @Cacheable(cacheNames = "missions", sync = true)
  SettlementMission findOne(Long id);

  @Override
  @CacheEvict(cacheNames = "missions")
  void delete(Long id);

  /**
   * 'p0' required in key because java does not retain parameter names during compilation unless
   * specified. You must use position parameter bindings otherwise.
   */
  @Override
  @CacheEvict(cacheNames = "missions", key = "#p0.getId()")
  <S extends SettlementMission> S save(S entity);
}
