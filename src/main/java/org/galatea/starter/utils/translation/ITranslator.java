package org.galatea.starter.utils.translation;

/**
 * Use this to implement translators that can convert from one type to another. This is to be used
 * for message conversion to/from internal domain objects.
 *
 * @param <T> the type being converted from
 * @param <U> the type being converted to
 */
@FunctionalInterface
public interface ITranslator<T, U> {

  /**
   * Converts the input parameter into the output type.
   */
  U translate(T source);
}
