package org.galatea.starter.domain.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data // Tells Lombok to generate getters/setters for all fields below
@Builder
@AllArgsConstructor
public class StockMetadata {

  private String timeStamp;
  private String responseTime;
  private String processTime;
  private String endpoint;
  private String host;

  private StockMetadata (){

  }

}
