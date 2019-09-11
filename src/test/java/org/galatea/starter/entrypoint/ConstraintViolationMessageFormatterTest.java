package org.galatea.starter.entrypoint;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.Test;

public class ConstraintViolationMessageFormatterTest {

  @Test
  public void violationMessagesConcatenated() {
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violationWithMessage("msg1"));
    violations.add(violationWithMessage("msg2"));

    ConstraintViolationException exception = new ConstraintViolationException(violations);

    String message = ConstraintViolationMessageFormatter.toMessage(exception);

    violations.forEach(violation -> assertTrue(message.contains(violation.getMessage())));
  }

  private ConstraintViolation violationWithMessage(String message) {
    ConstraintViolation violation = mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn(message);
    return violation;
  }

}