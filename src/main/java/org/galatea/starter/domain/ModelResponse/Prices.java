package org.galatea.starter.domain.ModelResponse;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Prices {

  @JsonProperty ("1. open")
  private double open;
  @JsonProperty ("2. high")
  private double high;
  @JsonProperty ("3. low")
  private double low;
  @JsonProperty ("4. close")
  private double close;
  @JsonProperty ("5. volume")
  private double volume;
  @JsonProperty("6. adjusted close")
  private double adjustedClose;
  @JsonProperty("7. dividend amount")
  private double  dividendAmount;
  @JsonProperty("8. split coefficient")
  private double splitCoefficient;

}
