package org.galatea.starter.utils.rest;

import static org.galatea.starter.entrypoint.BaseRestController.EXTERNAL_REQUEST_ID;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.function.Predicate;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.actuate.trace.http.HttpExchangeTracer;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

/**
 * Builds upon spring actuator's web request tracer to capture interesting audit information. We
 * capture some additional timing data as well The filter also adds these audit fields as headers to
 * the response.
 *
 * @author rbasu
 */
@ToString
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class FuseHttpTraceFilter extends HttpTraceFilter {

  private static final String INTERNAL_REQUEST_ID = "internal-request-id";

  private static final Random QUERY_ID_GENERATOR = new Random();

  @NonNull
  protected final Predicate<String> pathsToSkip;

  /**
   * Sadly we have to write our own constructor since lombok can't call super with args.
   *
   * @param repository the repository where we store our trace
   * @param pathsToSkip a predicate that will return try if we want to a skip a certain url
   *     path
   */
  public FuseHttpTraceFilter(final HttpTraceRepository repository, final HttpExchangeTracer tracer,
      final Predicate<String> pathsToSkip) {
    super(repository, tracer);
    this.pathsToSkip = pathsToSkip;
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest request,
      final HttpServletResponse response, final FilterChain filterChain)
      throws ServletException, IOException {

    // generate the internal request Id
    // we want positive numbers only, so use nextInt(upperBound)
    String internallyGeneratedId =
        Integer.toString(QUERY_ID_GENERATOR.nextInt(Integer.MAX_VALUE));

    log.debug("Created internal request id: {}", internallyGeneratedId);

    // And add to MDC so it will show up in the logs
    // The key used here must align with the key defined in the logging
    // config's log-pattern
    MDC.put(INTERNAL_REQUEST_ID, internallyGeneratedId + " - ");

    // Skip paths that are not interesting to trace
    if (pathsToSkip.test(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    boolean isFirstRequest = !isAsyncDispatch(request);
    HttpServletRequest requestToUse = request;
    HttpServletResponse responseToUse = response;

    // We need to do this for the request and response since you can only read the stream that holds
    // the payload once
    if (isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
      requestToUse = new ContentCachingRequestWrapper(request);
    }
    if (isFirstRequest && !(response instanceof ContentCachingRequestWrapper)) {
      responseToUse = new ContentCachingResponseWrapper(response);
    }

    doFilterInternalHelper(requestToUse, responseToUse, filterChain, Instant.now());
  }

  @SneakyThrows
  // what's this method responsible for?
  protected void doFilterInternalHelper(final HttpServletRequest request,
      final HttpServletResponse response, final FilterChain filterChain,
      final Instant requestReceivedTime) {

    try {
      super.doFilterInternal(request, response, filterChain);
    } finally {
      addAuditHeaders(requestReceivedTime.toString(), response);
      updateResponse(response);
      MDC.clear();
    }
  }

  private void addAuditHeaders(final String requestReceivedTime,
      final HttpServletResponse response) {
    log.info("Attempting to add audit headers");
    String internalQueryId = MDC.get(INTERNAL_REQUEST_ID);
    if (internalQueryId != null) {
      logAndAddAuditHeader(response, "internalQueryId",
          internalQueryId.replace(" - ", "")); // internalQueryId has a ' - ' in MDC
    }
    String externalQueryId = MDC.get(EXTERNAL_REQUEST_ID);
    if (externalQueryId != null) {
      logAndAddAuditHeader(response, "externalQueryId",
          externalQueryId.replace(" - ", "")); // externalQueryId has a ' - ' in MDC
    }

    logAndAddAuditHeader(response, "requestReceivedTime", requestReceivedTime);

    String requestElapsedTimeMillis =
        String.valueOf(Instant.parse(requestReceivedTime).until(Instant.now(), ChronoUnit.MILLIS));
    logAndAddAuditHeader(response, "requestElapsedTimeMillis", requestElapsedTimeMillis);
  }

  /**
   * Logs header name/value and adds them to the response.
   */
  private void logAndAddAuditHeader(final HttpServletResponse response, final String headerName,
      final String headerValue) {
    log.debug("Adding audit header {}={}", headerName, headerValue);
    if (headerValue == null) {
      log.debug("Not adding header {} with null value", headerName);
    } else {
      response.addHeader(headerName, headerValue);
    }
  }

  /**
   * Updates the response because we add headers *after* the filter has been applied. This way we
   * ensure that any fields that were created during request handling (e.g. externalQueryId) will
   * make it onto the response.
   */
  private void updateResponse(final HttpServletResponse response) throws IOException {
    ContentCachingResponseWrapper responseWrapper =
        WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
    responseWrapper.copyBodyToResponse();
  }
}
