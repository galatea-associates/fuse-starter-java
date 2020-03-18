package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity

public class PriceData {

  //Within each Date object, we have variables for the values of each price and the volume
  private BigDecimal open;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal close;
  private BigInteger volume;

  public PriceData (
      @JsonProperty("1. open") BigDecimal open,
      @JsonProperty("2. high") BigDecimal high,
      @JsonProperty("3. low") BigDecimal low,
      @JsonProperty("4. close") BigDecimal close,
      @JsonProperty("5. volume") BigInteger volume) {
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.volume = volume;
  }
}
