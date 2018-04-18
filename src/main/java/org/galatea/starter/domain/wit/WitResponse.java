package org.galatea.starter.domain.wit;

import lombok.Data;
import lombok.NonNull;

@Data
public class WitResponse {

  @NonNull
  private String _text;

  @NonNull
  private EntityStore entities;

  private String msg_id;

}
