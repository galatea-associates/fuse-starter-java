package org.galatea.starter.domain;

import org.galatea.starter.MappableEnum;

import java.util.Arrays;
import java.util.Collection;

public enum Direction implements MappableEnum {

  RECEIVE("REC"),
  DELIVER("DEL");

  private Collection<String> mappings;

  Direction(String... mappings) {
    this(Arrays.asList(mappings));
  }

  Direction(Collection<String> mappings) {
    this.mappings = mappings;
  }

  public Collection<String> getMappings() {
    return mappings;
  }

}
