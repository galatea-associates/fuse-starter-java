package org.galatea.starter.entrypoint.exception;

import java.util.Collection;

public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(Class<?> type, String identifier) {
    super("Entity " + identifier + " of type " + type.getSimpleName() + " was not found");
  }

  public EntityNotFoundException(Class<?> type, Collection<?> identifier) {
    super("Entities " + identifier + " of type " + type.getSimpleName() + " were not found");
  }

}
