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

  @JsonProperty("1. open")
  private BigDecimal open;

  @JsonProperty("2. high")
  private BigDecimal high;

  @JsonProperty("3. low")
  private BigDecimal low;

  @JsonProperty("4. close")
  private BigDecimal close;

  @JsonProperty("5. volume")
  private BigInteger volume;

}
