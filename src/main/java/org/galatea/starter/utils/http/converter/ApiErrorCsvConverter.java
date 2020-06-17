package org.galatea.starter.utils.http.converter;

import java.io.IOException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.galatea.starter.MvcConfig;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.entrypoint.ApiError;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionList;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

@Slf4j
public class ApiErrorCsvConverter extends AbstractHttpMessageConverter<ApiError> {

  /**
   * Construct an ApiErrorCsvConverter that supports conversion from ApiError to CSV.
   */
  public ApiErrorCsvConverter() {
    super(MvcConfig.TEXT_CSV);
  }

  @Override
  protected boolean supports(final Class<?> clazz) {
    // Use .isAssignableFrom() instead of .equals() if this converter can support subclasses
    // Be careful using .isAssignableFrom(), because new subclasses may be added in the future that
    // aren't supported by the conversion logic in this converter
    return ApiError.class.equals(clazz);
  }

  @Override
  protected ApiError readInternal(
      final Class<? extends ApiError> clazz,
      final HttpInputMessage inputMessage) throws IOException {
    throw new UnsupportedOperationException("Reading CSV to ApiError is not supported");
  }

  @Override
  protected void writeInternal(final ApiError apiError,
      final HttpOutputMessage outputMessage) throws IOException {
    log.info("Converting ApiError to CSV for HTTP response");
    outputMessage.getBody().write(CsvSerializer.serializeToCsv(Collections.singletonList(apiError),
        ApiError.class).getBytes(Charsets.UTF_8));
    log.info("Converted ApiError to CSV");
  }

  /**
   * Add additional headers to the outgoing response when this converter is used.
   */
  // Has a default implementation in AbstractHttpMessageConverter, so overriding this method is only
  // needed when modifying headers
  @Override
  protected void addDefaultHeaders(final HttpHeaders headers,
      final ApiError messages,
      final MediaType contentType) throws IOException {
    super.addDefaultHeaders(headers, messages, contentType);
    // Adding this header tells the browser to automatically download the response body
    // You may or may not want this depending on what your front end or other consumer wants
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=SettlementMissions.csv");
  }
}
