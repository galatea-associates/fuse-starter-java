package org.galatea.starter.utils.exception;

/**
 * Thrown when option argument expected via command line is not found.
 */
public class MissingOptionException extends RuntimeException {

  /**
   * Exception indicating expected command line argument not found.
   *
   * @param message exception message
   */
  public MissingOptionException(final String message) {
    super(message);
  }
}
