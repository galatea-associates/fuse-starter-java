package org.galatea.starter.domain.rpsy;

import java.util.List;
import java.util.Optional;
import org.galatea.starter.domain.SettlementMission;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

public interface ISettlementMissionRpsy extends CrudRepository<SettlementMission, Long> {

  /**
   * Retrieves all entities with the given depot.
   */
  List<SettlementMission> findByDepot(String depot);

  @Override
  @Cacheable(cacheNames = "missions", sync = true)
  Optional<SettlementMission> findById(Long id);

  @Override
  @CacheEvict(cacheNames = "missions")
  void deleteById(Long id);

  /**
   * 'p0' required in key because java does not retain parameter names during compilation unless
   * specified. You must use position parameter bindings otherwise.
   */
  @Override
  @CacheEvict(cacheNames = "missions", key = "#p0.getId()")
  <S extends SettlementMission> S save(S entity);
}
