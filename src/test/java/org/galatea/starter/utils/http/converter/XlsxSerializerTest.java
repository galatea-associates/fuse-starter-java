package org.galatea.starter.utils.http.converter;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.galatea.starter.testutils.XlsxComparator;
import org.junit.Test;

@Slf4j
public class XlsxSerializerTest {

  @Test
  public void serializeToXlsx_basic() throws Exception {
    BasicClass row1 = new BasicClass("foo", "bar");
    BasicClass row2 = new BasicClass("baz", null);

    byte[] expected;
    try (Workbook wbExpected = new XSSFWorkbook()) {
      Sheet sheet = wbExpected.createSheet();
      Row row = sheet.createRow(0);
      row.createCell(0).setCellValue("field1");
      row.createCell(1).setCellValue("field2");
      row = sheet.createRow(1);
      row.createCell(0).setCellValue("foo");
      row.createCell(1).setCellValue("bar");
      row = sheet.createRow(2);
      row.createCell(0).setCellValue("baz");
      row.createCell(1).setCellValue("");

      expected = writeSpreadsheetToBytes(wbExpected);
    }
    byte[] actual = XlsxSerializer.serializeToXlsx(Arrays.asList(row1, row2), BasicClass.class);
    assertTrue(XlsxComparator.equals(expected, actual));
  }

  @Test
  public void serializeToXlsx_collections() throws Exception {
    ClassWithCollections row1 = new ClassWithCollections(
        new int[] {1, 2}, new String[] {"foo", "bar"}, Arrays.asList("baz", "qux"));

    byte[] expected;
    try (Workbook wbExpected = new XSSFWorkbook()) {
      Sheet sheet = wbExpected.createSheet();
      Row row = sheet.createRow(0);
      row.createCell(0).setCellValue("field1");
      row.createCell(1).setCellValue("field2");
      row.createCell(2).setCellValue("field3");
      row = sheet.createRow(1);
      row.createCell(0).setCellValue("[1, 2]");
      row.createCell(1).setCellValue("[foo, bar]");
      row.createCell(2).setCellValue("[baz, qux]");

      expected = writeSpreadsheetToBytes(wbExpected);
    }
    byte[] actual = XlsxSerializer.serializeToXlsx(
        Collections.singletonList(row1), ClassWithCollections.class);
    assertTrue(XlsxComparator.equals(expected, actual));
  }

  @Test
  public void serializeToXslx_childClass() throws Exception {
    ChildClass row1 = new ChildClass("foo", "bar", "baz");

    byte[] expected;
    try (Workbook wbExpected = new XSSFWorkbook()) {
      Sheet sheet = wbExpected.createSheet();
      Row row = sheet.createRow(0);
      row.createCell(0).setCellValue("field1");
      row.createCell(1).setCellValue("field2");
      row.createCell(2).setCellValue("field3");
      row = sheet.createRow(1);
      row.createCell(0).setCellValue("foo");
      row.createCell(1).setCellValue("bar");
      row.createCell(2).setCellValue("baz");

      expected = writeSpreadsheetToBytes(wbExpected);
    }
    byte[] actual = XlsxSerializer.serializeToXlsx(
        Collections.singletonList(row1), ChildClass.class);
    assertTrue(XlsxComparator.equals(expected, actual));
  }

  @Test
  public void serializeToXslx_childClass2() throws Exception {
    BasicClass row1 = new ChildClass("foo", "bar", "baz");

    byte[] expected;
    try (Workbook wbExpected = new XSSFWorkbook()) {
      // Note that field3 isn't included because we're treating this as a BasicClass
      Sheet sheet = wbExpected.createSheet();
      Row row = sheet.createRow(0);
      row.createCell(0).setCellValue("field1");
      row.createCell(1).setCellValue("field2");
      row = sheet.createRow(1);
      row.createCell(0).setCellValue("foo");
      row.createCell(1).setCellValue("bar");

      expected = writeSpreadsheetToBytes(wbExpected);
    }
    byte[] actual = XlsxSerializer.serializeToXlsx(
        Collections.singletonList(row1), BasicClass.class);
    assertTrue(XlsxComparator.equals(expected, actual));
  }

  @Test
  public void serializeToXlsx_composedObject() throws Exception {
    ClassHoldingOtherClass row1 = new ClassHoldingOtherClass(1, new BasicClass("foo", "bar"));

    byte[] expected;
    try (Workbook wbExpected = new XSSFWorkbook()) {
      // Note that field3 isn't included
      Sheet sheet = wbExpected.createSheet();
      Row row = sheet.createRow(0);
      row.createCell(0).setCellValue("field1");
      row.createCell(1).setCellValue("field2");
      row = sheet.createRow(1);
      row.createCell(0).setCellValue("1");
      row.createCell(1).setCellValue("XlsxSerializerTest.BasicClass(field1=foo, field2=bar)");

      expected = writeSpreadsheetToBytes(wbExpected);
    }
    byte[] actual = XlsxSerializer.serializeToXlsx(
        Collections.singletonList(row1), ClassHoldingOtherClass.class);
    assertTrue(XlsxComparator.equals(expected, actual));
  }

  @Test
  public void serializeToXlsx_customHeaders() throws Exception {
    CustomHeadersClass row1 = new CustomHeadersClass("foo", "bar", "baz");

    byte[] expected;
    try (Workbook wbExpected = new XSSFWorkbook()) {
      Sheet sheet = wbExpected.createSheet();
      Row row = sheet.createRow(0);
      // Has custom header values, and field2 is ignored
      row.createCell(0).setCellValue("Header 1");
      row.createCell(1).setCellValue("Header 3");
      row = sheet.createRow(1);
      row.createCell(0).setCellValue("foo");
      row.createCell(1).setCellValue("baz");

      expected = writeSpreadsheetToBytes(wbExpected);
    }
    byte[] actual = XlsxSerializer.serializeToXlsx(
        Collections.singletonList(row1), CustomHeadersClass.class);
    assertTrue(XlsxComparator.equals(expected, actual));
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

  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  private static class BasicClass {

    private String field1;
    private String field2;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  private static class ClassWithCollections {

    private int[] field1;
    private String[] field2;
    private List<String> field3;
  }

  private static class ChildClass extends BasicClass {

    private String field3;

    ChildClass(final String field1, final String field2, final String field3) {
      super(field1, field2);
      this.field3 = field3;
    }
  }

  @AllArgsConstructor
  private static class ClassHoldingOtherClass {

    private int field1;
    private BasicClass field2;
  }

  @AllArgsConstructor
  private static class CustomHeadersClass {

    @JsonProperty("Header 1")
    private String field1;
    @JsonIgnore
    private String field2;
    @JsonProperty("Header 3")
    private String field3;
  }
}