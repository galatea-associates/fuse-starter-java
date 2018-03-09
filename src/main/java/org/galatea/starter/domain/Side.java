package org.galatea.starter.domain;

import org.galatea.starter.Mappable;

import java.util.Arrays;
import java.util.Collection;

public enum Side implements Mappable {

  BUY("B"),
  SELL("S"),
  SHORTSELL("SS");

  private Collection<String> mappings;

  Side(String... mappings) {
    this(Arrays.asList(mappings));
  }

  Side(Collection<String> mappings) {
    this.mappings = mappings;
  }

  @Override
  public Collection<String> getMappings() {
    return mappings;
  }

}
