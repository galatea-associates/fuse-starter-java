package org.galatea.starter.domain.ModelResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class Stock extends AlphaPrice {

  @JsonProperty("Time Series (Daily)")
  private AlphaPrice timeSeries;

}
