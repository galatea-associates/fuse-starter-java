package org.galatea.starter.utils.http.converter;

import java.io.IOException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.MvcConfig;
import org.galatea.starter.entrypoint.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

@Slf4j
public class ApiErrorXlsxConverter extends AbstractHttpMessageConverter<ApiError> {

  /**
   * Construct a ApiErrorXlsxConverter that supports an Excel MediaType.
   */
  public ApiErrorXlsxConverter() {
    super(MvcConfig.APPLICATION_EXCEL);
  }

  @Override
  protected boolean supports(final Class<?> clazz) {
    return ApiError.class.equals(clazz);
  }

  @Override
  protected ApiError readInternal(
      final Class<? extends ApiError> clazz,
      final HttpInputMessage inputMessage) throws IOException {
    throw new UnsupportedOperationException(
        "Reading XLSX to ApiError is not supported");
  }

  @SneakyThrows(IOException.class)
  @Override
  protected void writeInternal(final ApiError apiError, final HttpOutputMessage outputMessage) {
    log.info("Converting ApiError to XLSX for HTTP response");
    outputMessage.getBody().write(XlsxSerializer.serializeToXlsx(apiError, ApiError.class));
    log.info("Converted ApiError to XLSX");
  }

  /**
   * Add additional headers to the outgoing response when this converter is used.
   */
  @SneakyThrows(IOException.class)
  @Override
  protected void addDefaultHeaders(final HttpHeaders headers, final ApiError messages,
      final MediaType contentType) {
    super.addDefaultHeaders(headers, messages, contentType);
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ApiError.xlsx");
  }
}
