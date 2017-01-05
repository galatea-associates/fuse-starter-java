
package org.galatea.starter.entrypoint;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Sets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode(callSuper = true)
// We don't load the entire spring application context for this test.
@WebMvcTest(SettlementRestController.class)
@RunWith(DataProviderRunner.class)  //Use this runner since we want to parameterize certain tests
public class SettlementRestControllerTest extends ASpringTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private SettlementService mockSettlementService;

  private JacksonTester<List<TradeAgreement>> json;

  protected static final Long MISSION_ID_1 = 1091L;

  protected static final Long MISSION_ID_2 = 2091L;


  /**
   * Note that this is a static method.
   * 
   * @return each row represents a complete set of inputs to the test.  The first column is the agreement json and the second column has the expected mission ids.
   * 
   * @throws IOException if we can't read the input files
   */
  @DataProvider
  public static Object[][] settleAgreementDataProvider() throws IOException {
    return new Object[][] {
        {readData("Test_IBM_Two_Agreements.json"), Arrays.asList(MISSION_ID_2, MISSION_ID_1)},
        {readData("Test_IBM_Agreement.json"), Arrays.asList(MISSION_ID_1)}};
  }

  @Before
  public void setup() {
    ObjectMapper objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, objectMapper);
  }

  /**
   * Uses the method provided to the "use data provider" annotation to generate the inputs for this
   * test. This test is run once for each "row" returned by the data provider method.
   */
  @Test
  @UseDataProvider("settleAgreementDataProvider")
  public void testSettleAgreement(final String agreementJson, final List<Long> expectedMissionIds)
      throws Exception {
    String agreementJsonNoNl = agreementJson.replace("\n", "");
    log.info("Agreement json to post {}", agreementJsonNoNl);

    String expectedResponseJson =
        expectedMissionIds.stream().map(id -> "\"/settlementEngine/mission/" + id + "\"")
            .collect(Collectors.joining(",", "[", "]"));

    log.info("Expected json response {}", expectedResponseJson);

    List<TradeAgreement> agreements = json.parse(agreementJsonNoNl).getObject();
    log.info("Agreement objects that the service will expect {}", agreements);

    given(this.mockSettlementService.spawnMissions(agreements))
        .willReturn(Sets.newHashSet(expectedMissionIds));

    this.mvc
        .perform(post("/settlementEngine").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(agreementJsonNoNl))
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
