package org.galatea.starter.TickerInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticker {
    @JsonProperty("Meta Data")
    public MetaData metaData;
    @JsonProperty("Time Series (Daily)")
    public SortedMap<String, Day> timeSeries;

    public SortedMap<String, Day> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(SortedMap<String, Day> timeSeries) {
        this.timeSeries = timeSeries;
    }

    public Ticker(){

    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

}
