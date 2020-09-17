package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import java.time.OffsetTime;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galatea.starter.utils.MongoDocSerializer;

@JsonSerialize(using = MongoDocSerializer.class)
@NoArgsConstructor
@Data public class MongoDocument {
  //including extended hours
  public static OffsetTime NYSE_CLOSE_TIME_OFFSET = OffsetTime.parse("20:00:00-05:00");
  private Instant date;
  @JsonProperty(value = "1. open") private Double open;
  @JsonProperty(value = "2. high") private Double high;
  @JsonProperty(value = "3. low") private Double low;
  @JsonProperty(value = "4. close") private Double close;
}
