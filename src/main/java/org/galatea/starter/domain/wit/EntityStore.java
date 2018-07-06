package org.galatea.starter.domain.wit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode
@ToString
public class EntityStore {
  private Entity[] intent;
  private Entity[] stocks;
}

