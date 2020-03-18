package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor

public class MetaData {

  //Variables to define each element of the meta data
  private String information;
  private String symbol;
  private String lastRefreshed;
  private String outputSize;
  private String timeZone;

  public MetaData (
      @JsonProperty("1. Information") String information,
      @JsonProperty("2. Symbol") String symbol,
      @JsonProperty("3. Last Refreshed") String lastRefreshed,
      @JsonProperty("4. Output Size") String outputSize,
      @JsonProperty("5. Time Zone") String timeZone) {
    this.information = information;
    this.symbol = symbol;
    this.lastRefreshed = lastRefreshed;
    this.outputSize = outputSize;
    this.timeZone = timeZone;
  }
}
