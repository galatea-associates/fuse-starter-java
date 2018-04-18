package org.galatea.starter.domain.wit;

import lombok.Data;

@Data
public class EntityStore {
  private Entity[] intent;
  private Entity[] stocks;
}

