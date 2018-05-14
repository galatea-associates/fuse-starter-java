package org.galatea.starter.domain.wit;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;


@Data
@EqualsAndHashCode
@ToString
public class EntityStore {
  private Entity[] intent;
  private Entity[] stocks;
}

