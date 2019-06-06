package org.galatea.starter.utils.http.converter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Slf4j
// See XlsxSerializerTest for example output
public class XlsxSerializer {

  /**
   * Serialize the given objects to an XLSX spreadsheet.
   *
   * <p>Doesn't do any special handling of nested complex objects or collections in the given
   * row objects - each field in the row object is basically toString()'ed and put in a cell.
   *
   * @param rows the row data that the spreadsheet should hold
   * @param clazz the class that is the type of the row data
   * @param <T> the type of the row data
   * @return the binary representation of the xlsx spreadsheet
   */
  public static <T> byte[] serializeToXlsx(final Iterable<T> rows, final Class<T> clazz)
      throws IOException {
    // XSSF is used for xlsx-format spreadsheets, HSSF is used for xls-format
    // SXSSF is the streaming version of XSSF, and is useful for working with large spreadsheets
    try (Workbook wb = new XSSFWorkbook()) {
      Sheet sheet = wb.createSheet(clazz.getSimpleName());
      List<Field> fieldsToSerialize = getFields(clazz);
      populateHeaderRow(sheet, getHeaderValues(fieldsToSerialize));
      populateDataRows(sheet, fieldsToSerialize, rows);
      return writeSpreadsheetToBytes(wb);
    }
  }

  /*
   * Get a list of all fields in the given class, including inherited and private fields, but
   * excluding any @JsonIgnore'd fields
   */
  private static List<Field> getFields(final Class<?> clazz) {
    List<Field> fields = new ArrayList<>();
    // getFields() gets all public fields in a class including inherited fields, while
    // getDeclaredFields() gets all fields in a class excluding inherited fields
    // To get all fields in the class, we do getDeclaredFields() all the way up the class hierarchy
    if (clazz.getSuperclass() != null) {
      fields = getFields(clazz.getSuperclass());
    }

    // Ignore any fields that have a @JsonIgnore annotation
    for (Field field : clazz.getDeclaredFields()) {
      JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
      if (jsonIgnore == null || !jsonIgnore.value()) {
        fields.add(field);
      }
    }
    return fields;
  }

  /*
   * Get the column headers that should be used for the given fields. If a @JsonProperty annotation
   * is present on a given field, use that annotation's value as the header, otherwise use the field
   * name.
   */
  private static List<String> getHeaderValues(final List<Field> fields) {
    List<String> headers = new ArrayList<>();
    for (Field field : fields) {
      // Could also use a custom annotation instead of JsonProperty if different configuration is
      // needed for JSON and XLSX serialization
      JsonProperty customHeaderNameAnnotation = field.getAnnotation(JsonProperty.class);
      String header = customHeaderNameAnnotation == null
          ? field.getName()
          : customHeaderNameAnnotation.value();
      headers.add(header);
    }
    return headers;
  }

  /*
   * Populate the first row of the given sheet using the given list of header values.
   */
  private static void populateHeaderRow(final Sheet sheet, final List<String> headers) {
    Row headerRow = sheet.createRow(0);
    for (int col = 0; col < headers.size(); col++) {
      Cell headerCell = headerRow.createCell(col);
      headerCell.setCellValue(headers.get(col));
    }
  }

  /*
   * Populate the rows of the given sheet using the given row data.
   */
  @SneakyThrows(IllegalAccessException.class)
  private static <T> void populateDataRows(final Sheet sheet, final List<Field> fieldsToSerialize,
      final Iterable<T> rows) {
    log.info("Fields to serialize: {}", fieldsToSerialize);
    int rowIndex = 1; // header is row 0
    for (T row : rows) {
      Row dataRow = sheet.createRow(rowIndex);
      rowIndex++;
      for (int col = 0; col < fieldsToSerialize.size(); col++) {
        Cell dataCell = dataRow.createCell(col);
        Object cellObject = FieldUtils.readField(fieldsToSerialize.get(col), row, true);
        dataCell.setCellValue(stringify(cellObject));
        log.info("Field {} has value {}", fieldsToSerialize.get(col), stringify(cellObject));
      }
    }
  }

  private static String stringify(Object obj) {
    if (obj == null) {
      return "";
    } else if (obj.getClass().isArray()) {
      // Default toString() of an array isn't useful, and a primitive array can't be automatically
      // cast to Object[], so we copy the possibly-primitive array into a Object[] so we can pass
      // it into Arrays.toString()
      // https://stackoverflow.com/questions/5606338/cast-primitive-type-array-into-object-array-in-java
      int len = Array.getLength(obj);
      Object[] objectArr = new Object[len];
      for (int i = 0; i < len; i++) {
        objectArr[i] = Array.get(obj, i);
      }
      return Arrays.toString(objectArr);
    } else {
      return obj.toString();
    }
  }

  private static byte[] writeSpreadsheetToBytes(final Workbook wb) throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      wb.write(baos);
      return baos.toByteArray();
    } catch (IOException e) {
      log.error("Unable to write spreadsheet to bytes", e);
      throw e;
    }
  }
}
