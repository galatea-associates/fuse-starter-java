package org.galatea.starter.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class IexLastTradedPrice {
  private String symbol;
  private BigDecimal price;
  private Integer size;
  private long time;
}
