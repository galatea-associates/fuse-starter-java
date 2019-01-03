package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.SettlementMission;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ISettlementMissionRpsy extends CrudRepository<SettlementMission, Long> {

  String MISSIONS_CACHE = "missions";

  List<SettlementMission> findByDepot(String depot);

  @Override
  @Cacheable(cacheNames = MISSIONS_CACHE, sync = true)
  SettlementMission findOne(Long id);

  @Override
  @CacheEvict(cacheNames = MISSIONS_CACHE)
  void delete(Long id);

  /**
   * 'p0' required in key because java does not retain parameter names during compilation unless
   * specified. You must use position parameter bindings otherwise.
   */
  @Override
  @CacheEvict(cacheNames = MISSIONS_CACHE, key = "#p0.getId()")
  <S extends SettlementMission> S save(S entity);
}
