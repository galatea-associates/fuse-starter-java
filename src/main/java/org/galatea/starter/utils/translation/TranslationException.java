package org.galatea.starter.utils.translation;

/**
 * Represents an error in translating two objects.
 */
public class TranslationException extends RuntimeException{

  public TranslationException(String message, Throwable cause) {
    super(message, cause);
  }
}
