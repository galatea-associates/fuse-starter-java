package org.galatea.starter.utils.rest;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableInt;
import org.galatea.starter.utils.FuseTraceRepository;
import org.galatea.starter.utils.Tracer;
import org.galatea.starter.utils.Tracer.AutoClosedTrace;
import org.springframework.boot.actuate.trace.TraceProperties;
import org.springframework.boot.actuate.trace.WebRequestTraceFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.function.Predicate;

import static org.galatea.starter.utils.Tracer.addTraceInfo;

/**
 * Builds upon spring actuator's web request tracer to capture interesting audit information. We
 * capture some additional timing data as well as the request/response payload (which is missing
 * from spring's default implementation).
 *
 * @author rbasu
 */
@ToString
@EqualsAndHashCode(callSuper = true)
@Slf4j
// TODO what is this class meant to do? Why's it exist? How's it work its mojo?
public class FuseWebRequestTraceFilter extends WebRequestTraceFilter {

  public static final String REQUEST_PAYLOAD = "request-payload";
  public static final String RESPONSE_PAYLOAD = "response-payload";
  public static final String SPRING_TRACE_INFO = "spring-trace";
  @NonNull protected final Integer maxPayloadLength;
  @NonNull protected final FuseTraceRepository repository;
  @NonNull protected final Predicate<String> pathsToSkip;

  /**
   * Sadly we have to write our own constructor since lombok can't call super with args.
   *
   * @param repository the respository where we store our trace
   * @param properties any trace properties
   * @param pathsToSkip a predicate that will return try if we want to a skip a certain url path
   * @param maxPayloadLength the max number of bytes of payload information that we want to capture
   *     in the trace
   */
  public FuseWebRequestTraceFilter(
      final FuseTraceRepository repository,
      final TraceProperties properties,
      final Predicate<String> pathsToSkip,
      final Integer maxPayloadLength) {
    super(repository, properties);
    this.repository = repository;
    this.pathsToSkip = pathsToSkip;
    this.maxPayloadLength = maxPayloadLength;
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

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

    this.doFilterInternalHelper(requestToUse, responseToUse, filterChain);
  }

  @SneakyThrows
  // what's this method responsible for?
  protected void doFilterInternalHelper(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain) {

    // Start our trace
    try (AutoClosedTrace t = new AutoClosedTrace(repository, this.getClass())) {

      // This is the default trace info that we get from spring
      Map<String, Object> springTraceInfo = super.getTrace(request);

      MutableInt status = new MutableInt(HttpStatus.INTERNAL_SERVER_ERROR.value());
      try {
        t.runAndTraceSuccess(
            "request",
            () -> {
              filterChain.doFilter(request, response);
              status.setValue(response.getStatus());
              return Void.TYPE;
            });
      } finally {
        enhanceTrace(
            springTraceInfo,
            request,
            status.intValue() == response.getStatus()
                ? response
                : new CustomStatusResponseWrapper(response, status.intValue()));
        addAuditHeaders(response);
        updateResponse(response);
      }
    }
  }

  /** Adds audit information about the request/response to the response headers. */
  private void addAuditHeaders(final HttpServletResponse response) {
    log.info("Attempting to add audit headers");
    logAndAddAuditHeader(
        response, "internalQueryId", (String) Tracer.get(Tracer.class, Tracer.INTERNAL_REQUEST_ID));
    logAndAddAuditHeader(
        response, "externalQueryId", (String) Tracer.get(Tracer.class, Tracer.EXTERNAL_REQUEST_ID));

    String requestReceivedTime = (String) Tracer.get(Tracer.class, Tracer.TRACE_START_TIME_UTC);
    logAndAddAuditHeader(response, "requestReceivedTime", requestReceivedTime);

    String requestElapsedTimeMillis =
        String.valueOf(Instant.parse(requestReceivedTime).until(Instant.now(), ChronoUnit.MILLIS));
    logAndAddAuditHeader(response, "requestElapsedTimeMillis", requestElapsedTimeMillis);
  }

  /** Logs header name/value and adds them to the response. */
  private void logAndAddAuditHeader(
      HttpServletResponse response, String headerName, String headerValue) {
    log.debug("Adding audit header {}={}", headerName, headerValue);
    if (headerValue == null) {
      log.debug("Not adding header {} with null value", headerName);
    } else {
      response.addHeader(headerName, headerValue);
    }
  }

  // TODO: What's this update about the response?
  private void updateResponse(final HttpServletResponse response) throws IOException {
    ContentCachingResponseWrapper responseWrapper =
        WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
    responseWrapper.copyBodyToResponse();
  }

  protected void enhanceTrace(
      final Map<String, Object> springTraceInfo,
      final HttpServletRequest request,
      final HttpServletResponse response) {

    // Add additional spring info to the trace
    super.enhanceTrace(springTraceInfo, response);

    // Now we copy the spring trace info to the fuse trace
    addTraceInfo(this.getClass(), SPRING_TRACE_INFO, springTraceInfo);

    // Get the request and response payload and add to our trace. Note that the request payload is
    // only extracted AFTER the REST method handler has completed. This is intentional and
    // necessary.
    // TODO: why is it necessary?
    ContentCachingRequestWrapper requestWapper =
        WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
    ContentCachingResponseWrapper responseWrapper =
        WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
    addTraceInfo(this.getClass(), REQUEST_PAYLOAD, getPayload(requestWapper));
    addTraceInfo(this.getClass(), RESPONSE_PAYLOAD, getPayload(responseWrapper));
  }

  protected String getPayload(final ContentCachingRequestWrapper wrapper) {
    return wrapper == null
        ? "null"
        : getPayload(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
  }

  protected String getPayload(final ContentCachingResponseWrapper wrapper) {
    return wrapper == null
        ? "null"
        : getPayload(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
  }

  protected String getPayload(final byte[] buf, final String encoding) {
    if (buf.length == 0) {
      return "";
    }

    int length = Math.min(buf.length, getMaxPayloadLength());
    String payload;
    try {
      payload = new String(buf, 0, length, encoding);
    } catch (UnsupportedEncodingException ex) {
      payload = "[unknown]";
      log.warn("Couldn't determine payload due to error", ex);
    }
    return payload;
  }

  public int getMaxPayloadLength() {
    return maxPayloadLength;
  }

  /**
   * Copied this one from the super class. I would have reused the super class's version but alas it
   * was private.
   *
   * @author rbasu
   */
  private static final class CustomStatusResponseWrapper extends HttpServletResponseWrapper {

    private final int status;

    public CustomStatusResponseWrapper(final HttpServletResponse response, final int status) {
      super(response);
      this.status = status;
    }

    @Override
    public int getStatus() {
      return this.status;
    }
  }
}
