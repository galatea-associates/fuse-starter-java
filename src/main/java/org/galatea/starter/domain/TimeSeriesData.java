package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder

public class TimeSeriesData {

  //Within time series we have date objects
  @JsonFormat(
      shape = Shape.STRING,
      pattern = "yyyy-MM-dd")
  private Date priceDate;

  public TimeSeriesData (
      @JsonProperty("yyyy-MM-dd") Date priceDate)
  {
    this.priceDate = priceDate;
  }

}
