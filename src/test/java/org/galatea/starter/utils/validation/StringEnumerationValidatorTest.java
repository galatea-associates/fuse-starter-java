package org.galatea.starter.utils.validation;

import static org.galatea.starter.utils.validation.StringEnumerationValidatorTest.TestSetup.TestEnum.VAL;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class StringEnumerationValidatorTest {

  private StringEnumerationValidator validator;

  @Before
  public void setUp() {
    StringEnumeration enumAnnotation = mock(StringEnumeration.class);
    when(enumAnnotation.enumClass()).thenReturn((Class) TestSetup.TestEnum.class);

    validator = new StringEnumerationValidator();
    validator.initialize(enumAnnotation);
  }

  @Test
  public void validEnumMember() {
    assertTrue(validator.isValid(VAL.name(), null));
  }

  @Test
  public void nullIsValid() {
    assertTrue(validator.isValid(null, null));
  }

  @Test
  public void invalidEnumMember() {
    assertFalse(validator.isValid("invalidVal", null));
  }

  static class TestSetup {

    enum TestEnum {
      VAL
    }

  }

}