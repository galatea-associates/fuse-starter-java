package org.galatea.starter.entrypoint;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.Tracer;
import org.galatea.starter.utils.rest.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@RestController
public class SettlementRestController {

	@NonNull
	@Autowired
	SettlementService settlementService;

	public static final String SETTLE_MISSION_PATH = "/settlementEngine";
	public static final String GET_MISSION_PATH = SETTLE_MISSION_PATH + "/mission/";

	// @PostMapping to link http POST requests to this method
	// @RequestBody to have the post request body deserialized into a list of TradeAgreement objects
	@PostMapping(value = SETTLE_MISSION_PATH, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponse> settleAgreement(@RequestBody final List<TradeAgreement> agreements,
			@RequestParam(value = "requestId", required = false) String requestId) {

		// if an external request id was provided, grab it
		processRequestId(requestId);

		Set<Long> missionIds = settlementService.spawnMissions(agreements);
		Set<String> missionIdUris = missionIds.stream().map(id -> GET_MISSION_PATH + id).collect(Collectors.toSet());

		// wrap response in RestResponse to get nice Audit details in the response
		return ResponseEntity.accepted().body(new RestResponse(missionIdUris));
	}

	// @GetMapping to link http GET requests to this method
	// @PathVariable to take the id from the path and make it available as a method argument
	// @RequestParam to take a parameter from the url (ex: http://url?requestId=3123)
	@GetMapping(value = GET_MISSION_PATH + "{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponse> getMission(@PathVariable final Long id,
			@RequestParam(value = "requestId", required = false) String requestId) {

		// if an external request id was provided, grab it
		processRequestId(requestId);

		Optional<SettlementMission> msn = settlementService.findMission(id);

		if (msn.isPresent()) {
			// wrap response in RestResponse to get nice Audit details in the response
			return ResponseEntity.ok(new RestResponse(msn.get()));
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/**
	 * Adds the specified requestId to the context for this request (if not null)
	 *
	 * @param requestId
	 *            the requestId
	 */
	private void processRequestId(String requestId) {
		if (requestId != null) {
			log.info("Request received with id: {}", requestId);
			Tracer.setExternalRequestId(requestId);
		}
	}

}
