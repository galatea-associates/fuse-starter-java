package org.galatea.starter.domain.internal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter // Tells Lombok to generate getters/setters for all fields below
@Builder
public class StockMetadata {

  private String timeStamp;
  private String responseTime;
  private String processTime;
  private String endpoint;
  private String host;

}
