package org.galatea.starter.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class Quote {
  private String quote;
  private String author;
  private String category;
}