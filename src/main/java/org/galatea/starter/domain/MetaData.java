package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import lombok.Data;

@Data
@Entity

public class MetaData {

  //Variables to define each element of the meta data
  @JsonProperty("1. Information")
  private String information;

  @JsonProperty("2. Symbol")
  private String symbol;

  @JsonProperty("3. Last Refreshed")
  private String lastRefreshed;

  @JsonProperty("4. Output Size")
  private String outputSize;

  @JsonProperty("5. Time Zone")
  private String timeZone;

}
