package org.galatea.starter.domain.Wit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.galatea.starter.domain.Wit.EntityStore;

@Data
public class WitResponse {

  private String _text;
  private EntityStore entities;
  private String msg_id;

}
