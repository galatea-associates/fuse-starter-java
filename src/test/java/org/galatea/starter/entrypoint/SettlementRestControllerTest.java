
package org.galatea.starter.entrypoint;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Sets;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.service.SettlementService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode(callSuper = true)
// We don't load the entire spring application context for this test.
@WebMvcTest(SettlementRestController.class)
public class SettlementRestControllerTest extends ASpringTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private SettlementService mockSettlementService;

  private JacksonTester<List<TradeAgreement>> json;

  protected static final Long MISSION_ID_1 = 1091L;

  protected static final Long MISSION_ID_2 = 2091L;

  @Before
  public void setup() {
    ObjectMapper objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, objectMapper);
  }

  @Test
  public void testSettleTwoAgreements() throws Exception {
    String agreementJson = readData("Test_IBM_Two_Agreements.json").replace("\n", "");
    log.info("Agreement json to post {}", agreementJson);

    String expectedResponseJson = "[\"/settlementEngine/mission/" + MISSION_ID_2
        + "\",\"/settlementEngine/mission/" + MISSION_ID_1 + "\"]";
    log.info("Expected json response {}", expectedResponseJson);

    List<TradeAgreement> agreements = json.parse(agreementJson).getObject();
    log.info("Agreement objects that the service will expect {}", agreements);

    given(this.mockSettlementService.spawnMissions(agreements))
        .willReturn(Sets.newHashSet(MISSION_ID_1, MISSION_ID_2));

    this.mvc
        .perform(post("/settlementEngine").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(agreementJson))
        .andExpect(status().isAccepted()).andExpect(content().string(expectedResponseJson));

  }

  @Test
  public void testSettleOneAgreement() throws Exception {
    String agreementJson = readData("Test_IBM_Agreement.json").replace("\n", "");
    log.info("Agreement json to post {}", agreementJson);

    String expectedResponseJson = "[\"/settlementEngine/mission/" + MISSION_ID_1 + "\"]";
    log.info("Expected json response {}", expectedResponseJson);

    List<TradeAgreement> agreements = json.parse(agreementJson).getObject();
    log.info("Agreement objects that the service will expect {}", agreements);

    given(this.mockSettlementService.spawnMissions(agreements))
        .willReturn(Sets.newHashSet(MISSION_ID_1));

    this.mvc
        .perform(post("/settlementEngine").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(agreementJson))
        .andExpect(status().isAccepted()).andExpect(content().string(expectedResponseJson));

  }

  @Test
  public void testGetMissionFound() throws Exception {
    SettlementMission testMission = SettlementMission.builder().id(MISSION_ID_1).depot("DTC")
        .externalParty("EXT-1").instrument("IBM").direction("REC").qty(100.0).build();
    log.info("Test mission: {}", testMission);

    String expectedJson = readData("Test_IBM_Mission.json").replace("\n", "");
    log.info("Json expected: {}", expectedJson);

    given(this.mockSettlementService.findMission(MISSION_ID_1))
        .willReturn(Optional.of(testMission));

    this.mvc
        .perform(get("/settlementEngine/mission/" + MISSION_ID_1)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk()).andExpect(content().string(expectedJson));
  }

  @Test
  public void testGetMissionNotFound() throws Exception {
    long msnId = 1091L;

    given(this.mockSettlementService.findMission(msnId)).willReturn(Optional.empty());

    this.mvc
        .perform(get("/settlementEngine/mission/" + msnId).accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound()).andExpect(content().string(""));
  }


}
