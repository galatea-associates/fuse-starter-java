
package org.galatea.starter.entrypoint;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

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
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

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

		String expectedResponseJson = "[\"/settlementEngine/mission/" + MISSION_ID_2 + "\",\"/settlementEngine/mission/"
				+ MISSION_ID_1 + "\"]";
		log.info("Expected json response {}", expectedResponseJson);

		String mission1 = "/settlementEngine/mission/" + MISSION_ID_1;
		String mission2 = "/settlementEngine/mission/" + MISSION_ID_2;

		List<TradeAgreement> agreements = json.parse(agreementJson).getObject();
		log.info("Agreement objects that the service will expect {}", agreements);

		given(this.mockSettlementService.spawnMissions(agreements))
				.willReturn(Sets.newHashSet(MISSION_ID_1, MISSION_ID_2));

		ResultActions resultActions = this.mvc
				.perform(post("/settlementEngine").contentType(MediaType.APPLICATION_JSON_VALUE).content(agreementJson))
				.andExpect(status().isAccepted()).andExpect(jsonPath("$.response", contains(mission2, mission1)));

		verifyAuditFields(resultActions);
	}

	@Test
	public void testSettleOneAgreement() throws Exception {
		String agreementJson = readData("Test_IBM_Agreement.json").replace("\n", "");
		log.info("Agreement json to post {}", agreementJson);

		String expectedMission1ResponseJson = "/settlementEngine/mission/" + MISSION_ID_1;

		log.info("Expected json response {}", expectedMission1ResponseJson);

		List<TradeAgreement> agreements = json.parse(agreementJson).getObject();
		log.info("Agreement objects that the service will expect {}", agreements);

		given(this.mockSettlementService.spawnMissions(agreements)).willReturn(Sets.newHashSet(MISSION_ID_1));

		ResultActions resultActions = this.mvc
				.perform(post("/settlementEngine").contentType(MediaType.APPLICATION_JSON_VALUE).content(agreementJson))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.response", contains(expectedMission1ResponseJson)));

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

		given(this.mockSettlementService.findMission(MISSION_ID_1)).willReturn(Optional.of(testMission));

		ResultActions resultActions = this.mvc
				.perform(get("/settlementEngine/mission/" + MISSION_ID_1).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(jsonPath("$.response.id", is(MISSION_ID_1.intValue())))
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

		this.mvc.perform(get("/settlementEngine/mission/" + msnId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound()).andExpect(content().string(""));
	}

	/**
	 * Verifies required audit fields are present
	 *
	 * @param resultActions
	 *            The resultActions object wrapping the response
	 * @throws Exception
	 *             On any validation exception
	 */
	private void verifyAuditFields(ResultActions resultActions) throws Exception {
		resultActions.andExpect(jsonPath("$.audit", allOf(hasKey("requestReceivedTime"), hasKey("requestElapsedTime"),
				hasKey("externalQueryId"), hasKey("internalQueryId"))));
	}

}
