package org.galatea.starter.domain;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
public class WitAiIntents {

  private Long id;
  private String name;
  private BigDecimal confidence;
}
