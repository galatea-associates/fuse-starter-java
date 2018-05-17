package org.galatea.starter.domain.wit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;


@Data
@EqualsAndHashCode
@ToString
public class Entity {
  @NonNull
  private double confidence;
  @NonNull
  private String value;
  private String type;
}
