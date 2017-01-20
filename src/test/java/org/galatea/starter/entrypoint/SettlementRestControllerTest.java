package org.galatea.starter.entrypoint;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;

@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode(callSuper = true)
// We don't load the entire spring application context for this test.
@WebMvcTest(SettlementRestController.class)
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class SettlementRestControllerTest extends ASpringTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private SettlementService mockSettlementService;

  private JacksonTester<List<TradeAgreement>> agreementJsonTester;

  private JacksonTester<List<Long>> missionIdJsonTester;

  protected static final Long MISSION_ID_1 = 1091L;

  protected static final Long MISSION_ID_2 = 2091L;

  @Before
  public void setup() {
    ObjectMapper objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, objectMapper);
  }

  @Test
  @FileParameters(value = "src/test/resources/testSettleAgreement.data",
      mapper = JsonTestFileMapper.class)
  public void testSettleAgreement(final String agreementJson, final String expectedMissionIdJson)
      throws Exception {

    log.info("Agreement json to post {}", agreementJson);

    List<Long> expectedMissionIds = missionIdJsonTester.parse(expectedMissionIdJson).getObject();

    List<String> expectedResponseJsonList = expectedMissionIds.stream()
        .map(id -> "/settlementEngine/mission/" + id).collect(Collectors.toList());

    log.info("Expected json response {}", expectedResponseJsonList);

    List<TradeAgreement> agreements = agreementJsonTester.parse(agreementJson).getObject();
    log.info("Agreement objects that the service will expect {}", agreements);

    given(this.mockSettlementService.spawnMissions(agreements))
        .willReturn(Sets.newTreeSet(expectedMissionIds));

    ResultActions resultActions = this.mvc
        .perform(post("/settlementEngine").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(agreementJson))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.response", containsInAnyOrder(expectedResponseJsonList.toArray())));

    verifyAuditFields(resultActions);
  }

  @Test
  public void testGetMissionFound() throws Exception {
    String depot = "DTC";
    String externapParty = "EXT-1";
    String instrument = "IBM";
    String direction = "REC";
    double qty = 100;

    SettlementMission testMission = SettlementMission.builder().id(MISSION_ID_1).depot(depot)
        .externalParty(externapParty).instrument(instrument).direction(direction).qty(qty).build();
    log.info("Test mission: {}", testMission);


    given(this.mockSettlementService.findMission(MISSION_ID_1))
        .willReturn(Optional.of(testMission));

    ResultActions resultActions = this.mvc
        .perform(get("/settlementEngine/mission/" + MISSION_ID_1)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.response.id", is(MISSION_ID_1.intValue())))
        .andExpect(jsonPath("$.response.externalParty", is(externapParty)))
        .andExpect(jsonPath("$.response.instrument", is(instrument)))
        .andExpect(jsonPath("$.response.direction", is(direction)))
        .andExpect(jsonPath("$.response.qty", is(qty)));

    verifyAuditFields(resultActions);
  }

  @Test
  public void testGetMissionNotFound() throws Exception {
    long msnId = 1091L;

    given(this.mockSettlementService.findMission(msnId)).willReturn(Optional.empty());

    this.mvc
        .perform(get("/settlementEngine/mission/" + msnId).accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound()).andExpect(content().string(""));
  }

  /**
   * Verifies required audit fields are present
   *
   * @param resultActions The resultActions object wrapping the response
   * @throws Exception On any validation exception
   */
  private void verifyAuditFields(ResultActions resultActions) throws Exception {
    resultActions.andExpect(jsonPath("$.audit", allOf(hasKey("requestReceivedTime"),
        hasKey("requestElapsedTime"), hasKey("externalQueryId"), hasKey("internalQueryId"))));
  }

}
