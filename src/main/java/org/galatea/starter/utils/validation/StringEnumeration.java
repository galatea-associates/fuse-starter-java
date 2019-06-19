package org.galatea.starter.utils.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * A custom annotation which marks the target as a candidate for validation.  The target will be
 * validated by the specified class, in this case the StringEnumerationValidator.
 *
 * <p>See https://dzone.com/articles/create-your-own-constraint-with-bean-validation-20 for more
 * info about custom validators.
 */
@Documented
@Constraint(validatedBy = StringEnumerationValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringEnumeration {

  /**
   * Returns the message that will be shown when the target holds invalid data.
   */
  String message() default "No enum value found";

  /**
   * Returns the group that the target String belongs to.
   *
   * <p>Having multiple groups allows us to apply different validation rules to different groups of
   * targets.
   */
  Class<?>[] groups() default {};

  /**
   * Returns metadata information that may be used by a validation client.
   */
  Class<? extends Payload>[] payload() default {};

  /**
   * Returns the enum holding the set of valid values for the target String.
   */
  Class<? extends Enum<?>> enumClass();

}
