package org.galatea.starter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StringEnumerationValidator implements ConstraintValidator<StringEnumeration, String> {

  private Set<String> values;

  @Override
  public void initialize(StringEnumeration stringEnumeration) {
    Class<? extends Enum<?>> enumSelected = stringEnumeration.enumClass();
    values = getNamesSet(enumSelected);
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || values.contains(value);
  }

  private static Set<String> getNamesSet(Class<? extends Enum<?>> e) {
    Enum<?>[] enums = e.getEnumConstants();
    return Arrays.stream(enums).map(Enum::name).collect(Collectors.toSet());
  }

}
