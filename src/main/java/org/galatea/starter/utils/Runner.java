package org.galatea.starter.utils;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

/**
 * A utility class that allows us to wrap code with common behavior (e.g. locking, timing). We use
 * this instead of an annotation when we don't want to create a separate method but rather want to
 * in-line the code in the existing method.
 *
 * @author rbasu
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true, chain = true)
@Slf4j
public class Runner {

  @Setter
  protected Lock lock;

  @NonNull
  protected Runnable op;

  @Setter
  protected StopWatch timer;

  @Setter
  protected String taskName = "";

  /**
   * Creates a Runner holding the given Runnable.
   */
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

  /**
   * Attaches the provided suffix to the current thread name and then invokes call.call().
   */
  public static <T> T setThreadAndCall(final Callable<T> call, final String suffix)
      throws Exception {
    if (suffix.isEmpty()) {
      return call.call();
    }

    String oldName = Thread.currentThread().getName();
    Thread.currentThread().setName(oldName + "-" + suffix);
    try {
      return call.call();
    } finally {
      Thread.currentThread().setName(oldName);
    }
  }
}
