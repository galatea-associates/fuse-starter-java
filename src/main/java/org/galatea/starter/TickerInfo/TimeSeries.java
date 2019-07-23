package org.galatea.starter.TickerInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSeries {

    @JsonProperty("2019-07-23")
    public Day date;

    public Day getDate() {
        return date;
    }

    public void setDate(Day date) {
        this.date = date;
    }
}
