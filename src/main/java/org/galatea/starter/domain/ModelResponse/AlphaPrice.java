package org.galatea.starter.domain.ModelResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AlphaPrice {

  @JsonProperty ("Time Series (Daily)")
  private String TimeSeriesDaily;

}