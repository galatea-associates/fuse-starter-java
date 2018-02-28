package org.galatea.starter.domain.exception;

public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(Class<?> type, String identifier) {
    super("Entity " + identifier + " of type " + type.getSimpleName() + " was not found");
  }

}
