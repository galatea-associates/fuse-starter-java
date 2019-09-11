package org.galatea.starter.entrypoint.messagecontracts;

import javax.xml.bind.annotation.XmlRootElement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
@Builder
@Data
@XmlRootElement(name = "settlementMission")
public class SettlementMissionMessage {

  protected Long id;
  protected String instrument;
  protected String externalParty;
  protected String depot;
  protected String direction;
  protected Double qty;
  protected Long version;
}
