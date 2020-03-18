package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import javax.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@Getter
@RequiredArgsConstructor

public class AlphaVantageResponse {

  //Within the Av response we have Meta Data and Time Series Data, and within Time Series Data we have price data
  private MetaData metaData;
  //private String metaDataTag;
  //private String date;
  private TimeSeriesData timeSeriesData;
  private Map<String,Object> timeSeriesDataTag; //mapping one string(Timeseriesdaily) to multiple strings (each date)
  //private Map<String,String> priceData;

  public AlphaVantageResponse (
      @JsonProperty("Meta Data") MetaData metaData,
      @JsonProperty("Time Series (Daily)") Map<String,Object> timeSeriesDataTag) {
    this.metaData = metaData;
    this.timeSeriesDataTag = timeSeriesDataTag;
  }

}
