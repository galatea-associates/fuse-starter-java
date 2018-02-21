package org.galatea.starter.utils;

/**
 * Helper interface for creating simple objects with values we control.
 * @param <T> the type of object being created.
 */
@FunctionalInterface
public interface ObjectSupplier<T> {
  T get();
}
