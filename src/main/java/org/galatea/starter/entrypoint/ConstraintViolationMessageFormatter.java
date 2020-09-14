package org.galatea.starter.entrypoint;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class ConstraintViolationMessageFormatter {

  private ConstraintViolationMessageFormatter() {
  }

  /**
   * Creates a String listing all the violations held in the given ConstraintViolationException.
   */
  public static String toMessage(final ConstraintViolationException exception) {
    StringBuilder sb = new StringBuilder();

    for (ConstraintViolation<?> cv : exception.getConstraintViolations()) {
      sb.append(cv.getMessage());
      sb.append(". ");
    }

    return sb.toString();
  }

}
