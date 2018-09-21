package org.galatea.starter.domain.wit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode
@ToString
public class Entity {
  @JsonProperty("confidence")
  private Double confidence;
  @JsonProperty("value")
  private String value;
  private String type;
}
