package org.galatea.starter.utils.rest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.galatea.starter.utils.Tracer;

import com.google.common.collect.Maps;

import lombok.Getter;

@Getter
/**
 * Ensures some common audit fields for each rest response
 */
public class RestResponse {

	private Map<String, Object> audit;
	private Object response;

	public RestResponse(Object responseObject) {
		this.response = responseObject;

		audit = Maps.newHashMap();
		// TODO: Do we like the interplay between this class and the Tracer?
		// should the Tracer expose some 'getAuditKeys' method or something to
		// drive what ends up in the Audit response?
		// or do we prefer for this RestResponse object to know about the Tracer
		// details?
		audit.put("internalQueryId", Tracer.get(Tracer.class, Tracer.INTERNAL_REQUEST_ID));
		audit.put("externalQueryId", Tracer.get(Tracer.class, Tracer.EXTERNAL_REQUEST_ID));
		Object startTime = Tracer.get(Tracer.class, Tracer.TRACE_START_TIME_UTC);
		audit.put("requestReceivedTime", startTime);
		audit.put("requestElapsedTime",
				Instant.parse((CharSequence) startTime).until(Instant.now(), ChronoUnit.MILLIS));
	}

}
