package org.galatea.starter.entrypoint.messagecontracts;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galatea.starter.domain.SettlementMission;

/**
 * This wrapper class is needed to support HttpMessageConverter implementations.
 *
 * <p>For XML conversion, this class is needed because XML needs a single root element. As far as
 * we've figured out (pending issue #221), we need to manually put a wrapper object around the List
 * in order to get that root element.
 *
 * <p>For custom implementations with conversion logic specific to a List of
 * SettlementMissionMessage instances, this class is needed because of how Java generics and the
 * HttpMessageConverter interface work. Note that HttpMessageConverter#canRead and #canWrite take in
 * a Class as an argument. If we used {@code List<SettlementMission>} in our controller method
 * signature, the argument value would just be List. Inside of those #canRead and #canWrite methods
 * we'd have no way of figuring out what's in the List. As a result, we need this wrapper to know
 * while inside HttpMessageConverter that we have a List of SettlementMissionMessage.
 *
 * <p>If only JSON needs to be supported as the Content Type of responses, a wrapper isn't
 * necessary and the controller method can just return a {@code List<SettlementMission>}.
 */
@AllArgsConstructor()
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
@Data
@XmlRootElement(name = "settlementMissions")
@XmlAccessorType(XmlAccessType.FIELD) // required if using lombok to avoid duplicate properties
public class SettlementMissionList {

  @XmlElement(name = "settlementMission")
  protected List<SettlementMission> settlementMissions;
}
