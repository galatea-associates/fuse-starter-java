package org.galatea.starter.utils.http.converter;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.MvcConfig;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionList;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

/**
 * Custom HttpMessageConverter implementation to automatically convert a SettlementMissionList to an
 * XLSX spreadsheet when returning the SettlementMissionList in an HTTP response.
 */
// See comments throughout SettlementMissionCsvConverter
@Slf4j
public class SettlementMissionXlsxConverter
    extends AbstractHttpMessageConverter<SettlementMissionList> {

  /**
   * Construct a SettlementMissionXlsxConverter that supports an Excel MediaType.
   */
  public SettlementMissionXlsxConverter() {
    super(MvcConfig.APPLICATION_EXCEL);
  }

  @Override
  protected boolean supports(final Class<?> clazz) {
    return SettlementMissionList.class.equals(clazz);
  }

  @Override
  protected SettlementMissionList readInternal(
      final Class<? extends SettlementMissionList> clazz,
      final HttpInputMessage inputMessage) throws IOException {
    throw new UnsupportedOperationException(
        "Reading XLSX to SettlementMissionList is not supported");
  }

  @Override
  protected void writeInternal(final SettlementMissionList settlementMissionList,
      final HttpOutputMessage outputMessage) throws IOException {
    log.info("Converting SettlementMissionList to XLSX for HTTP response");
    outputMessage.getBody().write(XlsxSerializer.serializeToXlsx(
        settlementMissionList.getSettlementMissions(), SettlementMission.class));
    log.info("Converted SettlementMissionList to XLSX");
  }

  /**
   * Add additional headers to the outgoing response when this converter is used.
   */
  @Override
  protected void addDefaultHeaders(final HttpHeaders headers,
      final SettlementMissionList messages,
      final MediaType contentType) throws IOException {
    super.addDefaultHeaders(headers, messages, contentType);
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=SettlementMissions.xlsx");
  }
}
