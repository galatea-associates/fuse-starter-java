package org.galatea.starter.domain.wit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;


@Data
@EqualsAndHashCode
@ToString
public class Entity {
  //@NonNull
  @JsonProperty("confidence")
  private Double confidence;
  //@NonNull
  @JsonProperty("value")
  private String value;
  private String type;
}
