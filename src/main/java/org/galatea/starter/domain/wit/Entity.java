package org.galatea.starter.domain.wit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Builder;

@Data
@EqualsAndHashCode
@ToString
@Builder
public class Entity {
  private Double confidence;
  private String value;
  private String type;
}
