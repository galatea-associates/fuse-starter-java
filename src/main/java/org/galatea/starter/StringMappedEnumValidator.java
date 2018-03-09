package org.galatea.starter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StringMappedEnumValidator implements
    ConstraintValidator<StringMappedEnum, String> {

  private Set<String> values;

  @Override
  public void initialize(StringMappedEnum stringMappedEnum) {
    Class<? extends Mappable> enumSelected = stringMappedEnum.enumClass();
    values = getNamesSet(enumSelected);
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || values.contains(value);
  }

  private static Set<String> getNamesSet(Class<? extends Mappable> e) {
    return Arrays.stream(e.getEnumConstants())
        .map(Mappable::getMappings).flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

}
