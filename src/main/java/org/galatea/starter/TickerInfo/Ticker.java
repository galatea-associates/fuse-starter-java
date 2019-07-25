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
    public HashMap<String, Day> timeSeries;

    public Ticker(){

    }

    public HashMap<String, Day> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(HashMap<String, Day> timeSeries) {
        this.timeSeries = timeSeries;
    }


    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

}
