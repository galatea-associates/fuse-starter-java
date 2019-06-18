package org.galatea.starter.domain.modelresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlphaStock {

  @JsonProperty("2. Symbol")
  private String stock;
}
