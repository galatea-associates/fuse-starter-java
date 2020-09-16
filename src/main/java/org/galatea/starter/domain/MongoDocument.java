package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galatea.starter.utils.MongoDocSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonSerialize(using = MongoDocSerializer.class)
@NoArgsConstructor
@Data
@Document
public class MongoDocument {
  @Id private String id;
  private String date;
  @JsonProperty(value = "1. open") private Double open;
  @JsonProperty(value = "2. high") private Double high;
  @JsonProperty(value = "3. low") private Double low;
  @JsonProperty(value = "4. close") private Double close;
}
