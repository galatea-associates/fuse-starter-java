package org.galatea.starter.testutils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

@Slf4j
public class XlsxComparator {

  /**
   * Compare the cell data of two XLSX spreadsheets, ignoring formatting differences.
   *
   * @param left the binary content of one spreadsheet
   * @param right the binary content of the other spreadsheet
   * @return whether the two spreadsheets have the same cell data
   */
  public static boolean equals(final byte[] left, final byte[] right) throws IOException {
    try (Workbook wbLeft = WorkbookFactory.create(new ByteArrayInputStream(left))) {
      try (Workbook wbRight = WorkbookFactory.create(new ByteArrayInputStream(right))) {
        return equals(wbLeft, wbRight);
      }
    }
  }

  private static boolean equals(final Workbook left, final Workbook right) {
    DataFormatter formatter = new DataFormatter();
    boolean equals = true;

    // Compare sheet counts
    int leftSheetCount = left.getNumberOfSheets();
    int rightSheetCount = right.getNumberOfSheets();
    if (leftSheetCount != rightSheetCount) {
      log.error("Spreadsheets have different sheet counts ({}, {})",
          leftSheetCount, rightSheetCount);
      // Toggle the boolean instead of returning immediately so all differences can be logged
      equals = false;
    }

    // Compare sheet contents
    for (int sheetInd = 0; sheetInd < Math.min(leftSheetCount, rightSheetCount); sheetInd++) {
      Sheet leftSheet = left.getSheetAt(sheetInd);
      Sheet rightSheet = right.getSheetAt(sheetInd);

      // Compare row contents
      // getLastRowNum() returns the 0-based index of the last row, and may return the index of
      // a seemingly-blank row after the last clearly-populated row (see getLastRowNum Javadoc)
      int leftRowCount = leftSheet.getLastRowNum() + 1;
      int rightRowCount = rightSheet.getLastRowNum() + 1;
      // Max instead of min so that if one count is higher we can check if those additional rows
      // are actually populated and then log their contents if so
      for (int rowInd = 0; rowInd < Math.max(leftRowCount, rightRowCount); rowInd++) {
        // Blank rows may be returned as null by getRow()
        Row leftRow = leftSheet.getRow(rowInd);
        Row rightRow = rightSheet.getRow(rowInd);

        // getLastCellNum() returns the 1-based index of the last cell (contrast with getLastRowNum)
        int leftColCount = leftRow == null ? 0 : leftRow.getLastCellNum();
        int rightColCount = rightRow == null ? 0 : rightRow.getLastCellNum();
        for (int colInd = 0; colInd < Math.max(leftColCount, rightColCount); colInd++) {
          String leftValue
              = leftRow == null ? "" : formatter.formatCellValue(leftRow.getCell(colInd));
          String rightValue
              = rightRow == null ? "" : formatter.formatCellValue(rightRow.getCell(colInd));
          if (!StringUtils.equals(leftValue, rightValue)) {
            log.error("Sheet {} row {} cell {} has different values: \"{}\", \"{}\"",
                sheetInd, rowInd, colInd, leftValue, rightValue);
            equals = false;
          }
        }
      }
    }

    return equals;
  }
}
