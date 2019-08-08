package org.galatea.starter.domain;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IexLastTradedPrice {
  private String symbol;
  private BigDecimal price;
  private Integer size;
  private long time;
}
