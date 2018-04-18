package org.galatea.starter.domain.wit;

import lombok.Data;
import lombok.NonNull;

@Data
public class Entity {

  @NonNull
  private double confidence;

  @NonNull
  private String value;

  private String type;

}
