package org.galatea.starter.domain.internal;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;

@Data
@AllArgsConstructor
@Log
@Slf4j
@Builder
public class FullResponse {

  private StockMetadata metaData;
  private Collection<StockPrices> prices;

  public FullResponse() {

  }
}