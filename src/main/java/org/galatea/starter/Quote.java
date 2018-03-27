package org.galatea.starter;


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
