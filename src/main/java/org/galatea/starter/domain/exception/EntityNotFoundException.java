package org.galatea.starter.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ResponseStatus allows us to return a HttpStatus and message when the exception is thrown.
 * However, attaching ResponseStatus results in the exception knowing about the REST layer, which
 * can result in a sloppy architecture with a brittle structure.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entity not found")
public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(String identifier) {
    super("Entity " + identifier + " was not found");
  }

}
