package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

//reference: https://stackoverflow.com/questions/47263236/how-to-parse-json-object-array-with-jackson-into-dto-in-springboot
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AVDay{

  @JsonProperty("1. open")
  private double open;

  @JsonProperty("2. high")
  private double high;

  @JsonProperty("3. low")
  private double low;

  @JsonProperty("4. close")
  private double close;

  @JsonProperty("5. volume")
  private double volume;

  public double getOpen() {
    return open;
  }
}