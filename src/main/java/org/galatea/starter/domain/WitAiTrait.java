package org.galatea.starter.domain;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class WitAiTrait {

  private String id;
  private String value;
  private BigDecimal confidence;

}
