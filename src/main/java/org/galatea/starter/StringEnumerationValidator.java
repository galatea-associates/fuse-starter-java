package org.galatea.starter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Ensures a given String is a member of a specified enum value set.
 */
public class StringEnumerationValidator implements ConstraintValidator<StringEnumeration, String> {

  private Set<String> values;

  @Override
  public void initialize(final StringEnumeration stringEnumeration) {
    Class<? extends Enum<?>> enumSelected = stringEnumeration.enumClass();
    values = getNamesSet(enumSelected);
  }

  @Override
  public boolean isValid(final String value, final ConstraintValidatorContext context) {
    return value == null || values.contains(value);
  }

  private static Set<String> getNamesSet(final Class<? extends Enum<?>> e) {
    Enum<?>[] enums = e.getEnumConstants();
    return Arrays.stream(enums).map(Enum::name).collect(Collectors.toSet());
  }

}
