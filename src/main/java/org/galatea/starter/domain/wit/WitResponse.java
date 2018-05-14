package org.galatea.starter.domain.wit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;


@Data
@EqualsAndHashCode
@ToString
public class WitResponse {
  @JsonProperty("_text")
  private String text;
  private EntityStore entities;
  private String msg_id;

}
