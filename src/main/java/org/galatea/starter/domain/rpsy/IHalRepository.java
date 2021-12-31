package org.galatea.starter.domain.rpsy;

import java.util.List;
import org.galatea.starter.domain.HalData;
import org.springframework.data.repository.CrudRepository;

public interface IHalRepository extends CrudRepository<HalData, Long> {

  List<HalData> findAllByIntent(String intent);

}
