package org.galatea.starter.domain;

import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
public class WitAiResolvedEntity {

  private String name;
  private String domain;
  private WitAiLocationCoordinates coords;
  private String timezone;
  private Map<String, String> external;
  private Map<String, String> attributes;
}
