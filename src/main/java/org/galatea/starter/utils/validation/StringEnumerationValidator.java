package org.galatea.starter.utils.validation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Verifies a String is a member of an enum value set.
 */
public class StringEnumerationValidator implements ConstraintValidator<StringEnumeration, String> {

  private Set<String> values;

  @Override
  public void initialize(final StringEnumeration stringEnumeration) {
    Class<? extends Enum<?>> enumClass = stringEnumeration.enumClass();
    values = getNamesSet(enumClass);
  }

  @Override
  public boolean isValid(final String value, final ConstraintValidatorContext context) {
    return value == null || values.contains(value);
  }

  private static Set<String> getNamesSet(final Class<? extends Enum<?>> enumClass) {
    Enum<?>[] enumConstants = enumClass.getEnumConstants();
    return Arrays.stream(enumConstants).map(Enum::name).collect(Collectors.toSet());
  }

}
