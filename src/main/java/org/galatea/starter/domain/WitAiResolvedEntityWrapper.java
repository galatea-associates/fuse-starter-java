package org.galatea.starter.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
public class WitAiResolvedEntityWrapper {

  List<WitAiResolvedEntity> values;

}
