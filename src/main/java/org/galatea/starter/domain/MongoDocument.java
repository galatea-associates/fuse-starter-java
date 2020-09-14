package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galatea.starter.utils.MongoDocSerializer;

@JsonSerialize(using = MongoDocSerializer.class)
@NoArgsConstructor
@Data public class MongoDocument {
  private String date;
  @JsonProperty(value = "1. open") private Double open;
  @JsonProperty(value = "2. high")private Double high;
  @JsonProperty(value = "3. low")private Double low;
  @JsonProperty(value = "4. close")private Double close;
}
