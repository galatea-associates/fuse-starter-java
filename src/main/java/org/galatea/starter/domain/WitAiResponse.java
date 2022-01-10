package org.galatea.starter.domain;

import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
public class WitAiResponse {

  private String text;
  private List<WitAiIntents> intents;
  private Map<String, List<WitAiEntity>> entities;
  private Map<String, List<WitAiTrait>> traits;

}
