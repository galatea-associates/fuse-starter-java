package org.galatea.starter.entrypoint.messagecontracts;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor(access = AccessLevel.PRIVATE) // For builder
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
@Builder
@Data
@Slf4j
@XmlRootElement(name = "settlementResponse")
@XmlAccessorType(XmlAccessType.FIELD) // required if using lombok to avoid duplicate properties
public class SettlementResponseMessage {

  @Singular
  @XmlElement(name = "spawnedMission")
  protected List<String> spawnedMissions;
}
