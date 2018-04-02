package org.galatea.starter.domain.Wit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
public class Entity {

  @NonNull
  private double confidence;

  @NonNull
  private String value;

  private String type;

}
