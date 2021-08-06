package org.galatea.starter.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
public class WorldTimeApiResponse {

  private String abbreviation;
  private String datetime;

}
