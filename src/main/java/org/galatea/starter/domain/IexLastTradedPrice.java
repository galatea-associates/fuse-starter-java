package org.galatea.starter.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IexLastTradedPrice {
  private String symbol;
  private long price;
  private Integer size;
  private long time;
}
