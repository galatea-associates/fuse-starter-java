package org.galatea.starter.domain.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter // Tells Lombok to generate getters/setters for all fields below
@AllArgsConstructor //Tells java to instantiate all fields listed

public class StockMetadata {

  private final String timeStamp;
  private final String responseTime;
  private final String endpoint;
  private final String host;

}
