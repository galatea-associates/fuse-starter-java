package org.galatea.starter.entrypoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This container object is required in order to return a set of Strings from the controller.
 * Note the XmlRootElement annotation, which the XML HttpMessageConverter uses to detect XML writing
 * capabilities of an object.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@XmlRootElement(name = "settlementResponse")
public class SettlementResponse {

  private Set<String> spawnedMissions;

}
