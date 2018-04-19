package org.galatea.starter.domain;


import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class Quote {

  @SerializedName("quote")
  private String quoteText;
  private String author;
  private String category;
}