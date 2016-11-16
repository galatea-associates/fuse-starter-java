
package org.galatea.starter.utils;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.StopWatch;

import java.util.concurrent.locks.Lock;

/**
 * A utility class that allows us to wrap code with common behavior (e.g. locking, timing). We use
 * this instead of an annotation when we don't want to create a separate method but rather want to
 * in-line the code in the existing method.
 *
 * @author rbasu
 *
 */
@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@ToString
@EqualsAndHashCode
public class Runner {

  @Setter
  protected Lock lock;

  @NonNull
  protected Runnable op;

  @Setter
  protected StopWatch timer;

  @Setter
  protected String taskName = "";

  public static Runner of(final Runnable op) {
    return new Runner(op);
  }

  /**
   * Runs the operation with the non-null decorators provided.
   *
   * @return a stopwatch that was used to time the operation. The stopwatch will be stopped.
   */
  public StopWatch run() {
    // If this has been provided with a timer, use it. Otherwise, create a new one each time run is
    // called.
    StopWatch swToUse = defaultIfNull(timer, new StopWatch());

    swToUse.start(taskName);
    wrapWithLock(op).run();
    swToUse.stop();

    return swToUse;
  }

  protected Runnable wrapWithLock(final Runnable opToBeWrapped) {
    if (this.lock == null) {
      return opToBeWrapped;
    }

    return () -> {
      lock.lock();
      try {
        opToBeWrapped.run();
      } finally {
        lock.unlock();
      }
    };
  }

}
