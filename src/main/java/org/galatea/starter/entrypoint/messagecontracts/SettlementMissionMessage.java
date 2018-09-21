package org.galatea.starter.entrypoint.messagecontracts;

import javax.xml.bind.annotation.XmlRootElement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
@Builder
@Data
@Slf4j
@XmlRootElement(name="settlementMission")
public class SettlementMissionMessage {
  protected Long id;
  protected String instrument;
  protected String externalParty;
  protected String depot;
  protected String direction;
  protected Double qty;
}
