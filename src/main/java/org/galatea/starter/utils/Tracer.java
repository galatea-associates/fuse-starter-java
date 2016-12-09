
package org.galatea.starter.utils;

import com.google.common.collect.Maps;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@ToString
@EqualsAndHashCode
public class Tracer {

  public static final String TRACE_START_TIME_UTC = "trace-start-UTC";
  public static final String TRACE_END_TIME_UTC = "trace-end-UTC";
  public static final String TRACE_SW_SUMMARY = "trace-timing-ms";

  // We make this one private since we don't want to encourage use of the stop watch outside this
  // class
  private static final String STOP_WATCH = "sw";


  private static final InheritableThreadLocal<Map<MultiKey<String>, Object>> traceInfo =
      new InheritableThreadLocal<Map<MultiKey<String>, Object>>() {
        @Override
        protected Map<MultiKey<String>, Object> childValue(
            final Map<MultiKey<String>, Object> parentValue) {
          if (parentValue == null) {
            return null;
          }
          return Maps.newHashMap(parentValue);
        }

        @Override
        protected Map<MultiKey<String>, Object> initialValue() {
          return Maps.newHashMap();
        }
      };


  private static MultiKey<String> keyOf(final Class<?> clz, final String key) {
    return new MultiKey<String>(clz.getSimpleName(), key);
  }

  public static void addTraceInfo(@NonNull final Class<?> clz, @NonNull final String key,
      @NonNull final Object val) {
    traceInfo.get().put(keyOf(clz, key), val);
  }

  public static Object get(@NonNull final Class<?> clz, @NonNull final String key) {
    return traceInfo.get().get(keyOf(clz, key));
  }

  public static void remove(@NonNull final Class<?> clz, @NonNull final String key) {
    traceInfo.get().remove(keyOf(clz, key));
  }

  /**
   * Flatten the trace info into a map of maps. This is useful when you add the trace info to a
   * trace repository.
   *
   */
  public static Map<String, Map<String, Object>> getFlattenedCopyOfTraceInfo() {
    Map<String, Map<String, Object>> flatMap =
        new HashMap<>(traceInfo.get().size());

    traceInfo.get().forEach((mk, obj) -> {
      String clzName = mk.getKey(0);
      String traceKey = mk.getKey(1);
      if (!clzName.isEmpty()) {
        flatMap.computeIfAbsent(clzName, name -> new HashMap<String, Object>()).put(traceKey, obj);
      }
    });

    return flatMap;
  }

  /**
   * Starts a brand new trace. Will clear any existing data in the trace map to make sure we are
   * starting with a clean slate.
   */
  private static void startTrace() {
    clearTrace();

    StopWatch sw = new StopWatch();
    sw.start();

    Map<MultiKey<String>, Object> map = traceInfo.get();
    map.put(new MultiKey<String>("", STOP_WATCH), sw);
    map.put(keyOf(Tracer.class, TRACE_START_TIME_UTC), Instant.now().toString());
  }

  /**
   * Stops the current trace. You can still add items to the trace context after the trace is
   * stopped but nothing else will be timed.
   */
  private static void stopTrace() {
    Map<MultiKey<String>, Object> map = traceInfo.get();

    StopWatch sw = getStopWatch();
    if (sw == null) {
      log.warn("No stopwatch found.  Are we sure this trace was started?");
      return;
    }

    if (sw.isRunning()) {
      sw.stop();
    }

    map.put(keyOf(Tracer.class, TRACE_END_TIME_UTC), Instant.now().toString());
    map.put(keyOf(Tracer.class, TRACE_SW_SUMMARY), sw.getTotalTimeMillis());
  }

  /**
   * Deletes all of the data in the current trace.
   */
  public static void clearTrace() {
    traceInfo.get().clear();
  }

  protected static StopWatch getStopWatch() {
    return (StopWatch) traceInfo.get()
        .get(new MultiKey<String>("", STOP_WATCH));
  }

  /**
   * This is a useful class for running a trace. It allows us to use a trace in a try with which
   * makes starting/stopping the trace easy.
   *
   * @author rbasu
   *
   */
  public static class AutoClosedTrace implements AutoCloseable {

    protected FuseTraceRepository rpsy;
    protected Class<?> clz;

    /**
     * Starts a new trace and automatically ends it when we exit the try-with block. Also stores the
     * results to the trace repository provided.
     *
     * @param rpsy the trace repository for the trace results.
     * @param clz the class we are initiating the trace from.
     */
    public AutoClosedTrace(final FuseTraceRepository rpsy, final Class<?> clz) {
      this.rpsy = rpsy;
      this.clz = clz;
      startTrace();
    }

    @Override
    public void close() {
      stopTrace();
      try {
        rpsy.addTraceInfo();
      } catch (Exception err) {
        log.warn("Could not save trace info. Discarding trace info.", err);
      }
      clearTrace();
    }

    /**
     * Will run the function provided and track success/failure (along with any associated error
     * message) in the current trace.
     *
     * @param traceKeyPrefix the prefix for the trace key
     * @param func the function we want to run
     * @return the results of the function we run
     * @throws Exception if something goes wrong
     */
    public <T> T runAndTraceSuccess(final String traceKeyPrefix, final Callable<T> func)
        throws Exception {
      try {
        T result = func.call();
        addTraceInfo(clz, traceKeyPrefix + "-success", "true");
        return result;
      } catch (Exception err) {
        addTraceInfo(clz, traceKeyPrefix + "-success", "false");
        addTraceInfo(clz, traceKeyPrefix + "-error", err.getMessage());
        throw err;
      }
    }

  }
}
