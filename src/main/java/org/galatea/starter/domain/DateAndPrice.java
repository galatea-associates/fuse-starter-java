package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;

@Data
public class DateAndPrice {

  public DateAndPrice (String date, double price) {
    this.date = date;
    this.price = price;
  }

  @JsonProperty
  private String date;

  @JsonProperty
  private double price;

}