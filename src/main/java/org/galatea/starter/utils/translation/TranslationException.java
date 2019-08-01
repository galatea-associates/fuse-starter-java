package org.galatea.starter.utils.translation;

/**
 * Represents an error in translating two objects.
 */
public class TranslationException extends RuntimeException {

  /**
   * Constructs a new TranslationException with the specified detail message and cause.
   */
  public TranslationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
