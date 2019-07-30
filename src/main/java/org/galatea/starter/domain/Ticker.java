package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "Tickers")
public class Ticker {

    @Id
    String id;

    @JsonProperty("Meta Data")
    public MetaData metaData;

    @JsonProperty("Time Series (Daily)")
    public SortedMap<String, Day> timeSeries;

    public Ticker(){
    }

    public SortedMap<String, Day> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(SortedMap <String, Day> timeSeries) {
        this.timeSeries = timeSeries;
    }


    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

}
