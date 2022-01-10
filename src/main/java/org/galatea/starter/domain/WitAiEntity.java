package org.galatea.starter.domain;

import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
public class WitAiEntity {

  private Long id;
  private String name;
  private String role;
  private Integer start;
  private Integer end;
  private String body;
  private BigDecimal confidence;
  private List<String> entities;
  private WitAiResolvedEntityWrapper resolved;
  private boolean suggested;
  private String value;
  private String type;
}
