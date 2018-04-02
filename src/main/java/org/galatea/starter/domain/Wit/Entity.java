package org.galatea.starter.domain.Wit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class Entity {
  private double confidence;
  private String value;
  private String type;

}
