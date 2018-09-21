package org.galatea.starter.domain.wit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode
@ToString
public class WitResponse {
  @JsonProperty("_text")
  private String text;
  @JsonProperty("entities")
  private EntityStore entities;
  @JsonProperty("msg_id")
  private String msgId;

}
