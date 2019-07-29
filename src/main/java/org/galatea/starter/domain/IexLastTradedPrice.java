package org.galatea.starter.domain;

import lombok.Data;

@Data
public class IexLastTradedPrice {
  private String symbol;
  private long price;
  private Integer size;
  private long time;
}
