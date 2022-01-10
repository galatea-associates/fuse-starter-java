package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
public class WitAiLocationCoordinates {

  private BigDecimal lat;

  @JsonProperty("long")
  private BigDecimal longitude;
}
