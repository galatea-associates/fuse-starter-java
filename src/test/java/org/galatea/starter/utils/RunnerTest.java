package org.galatea.starter.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.StopWatch;

@Slf4j
// Ignore warnings about Thread.sleep
// Yes, Thread.sleep leads to non-deterministic tests, but this class is already kind of complicated
// to test and trying to make these tests completely deterministic would probably be even more
// complicated, since properly testing this class requires things like starting one thread after
// another thread has reached a certain point
@SuppressWarnings("squid:S2925")
public class RunnerTest {

  @Test
  public void run_noConfig() {
    List<Integer> testList = Lists.newArrayList(1);
    Runnable op = () -> {
      sleepInRunnable(2L); // Ensure the task takes at least 1 ms
      testList.add(2);
    };
    Runner runner = Runner.of(op);

    StopWatch sw = runner.run();
    assertTrue(sw.getLastTaskTimeMillis() > 0L);
    assertEquals(Lists.newArrayList(1, 2), testList);
  }

  @Test
  public void run_withLock() throws Exception {
    List<Integer> testList = Lists.newArrayList(1);
    Runnable op1 = () -> {
      log.info("Starting op 1");
      sleepInRunnable(50L);
      testList.add(2);
      sleepInRunnable(50L);
      testList.add(3);
      log.info("Finished op 1");
    };
    Runnable op2 = () -> {
      log.info("Starting op 2");
      testList.add(4);
      testList.add(5);
      log.info("Finished op 2");
    };
    Runner runner1 = Runner.of(op1);
    Runner runner2 = Runner.of(op2);
    Lock sharedLock = new ReentrantLock();
    // This .lock() method is a setter, not a do-er
    // See the fluent=true property of the @Accessors annotation on Runner
    runner1.lock(sharedLock);
    runner2.lock(sharedLock);

    Runnable runner1Runnable = () -> {
      log.info("Starting thread 1");
      runner1.run();
    };
    Runnable runner2Runnable = () -> {
      log.info("Starting thread 2");
      runner2.run();
    };

    Thread thread1 = new Thread(runner1Runnable);
    Thread thread2 = new Thread(runner2Runnable);

    thread1.start();
    Thread.sleep(10L); // Give some time for thread 1 to start first
    thread2.start();
    thread1.join();
    thread2.join();

    assertEquals(Lists.newArrayList(1, 2, 3, 4, 5), testList);
  }

  @Test
  public void run_withStopwatch() throws Exception {
    List<Integer> testList = Lists.newArrayList(1);
    Runnable op = () -> {
      sleepInRunnable(2L); // Ensure the task takes at least 1 ms
      testList.add(2);
    };
    Runner runner = Runner.of(op);

    final long task1Time = 10L;
    StopWatch originalStopwatch = new StopWatch();
    originalStopwatch.start("task1");
    Thread.sleep(task1Time);
    originalStopwatch.stop();

    runner.timer(originalStopwatch);

    StopWatch returnedStopwatch = runner.run();
    assertTrue(returnedStopwatch.getLastTaskTimeMillis() > 0L);
    assertTrue(returnedStopwatch.getTotalTimeMillis() > task1Time);
    assertEquals(Lists.newArrayList(1, 2), testList);
  }

  @Test
  public void setThreadAndCall_noSuffix() throws Exception {
    Integer expectedReturn = 42;
    String baseThreadName = Thread.currentThread().getName();
    Callable<Integer> callable = () -> {
      log.info("Logging from inside the callable. This thread name should not have a suffix");
      assertEquals(baseThreadName, Thread.currentThread().getName());
      return expectedReturn;
    };

    Integer actual = Runner.setThreadAndCall(callable, "");
    assertEquals(expectedReturn, actual);
  }

  @Test
  public void setThreadAndCall_withSuffix() throws Exception {
    Integer expectedReturn = 42;
    String baseThreadName = Thread.currentThread().getName();
    String suffix = "suffix";
    Callable<Integer> callable = () -> {
      log.info("Logging from inside the callable. This thread name should end in -{}", suffix);
      assertEquals(baseThreadName + "-" + suffix, Thread.currentThread().getName());
      return expectedReturn;
    };

    Integer actual = Runner.setThreadAndCall(callable, suffix);
    assertEquals(expectedReturn, actual);
    assertEquals(baseThreadName, Thread.currentThread().getName());
  }

  // Runnables can't throw checked exceptions (which InterruptedException is), so use this method
  // to avoid adding try-catch blocks to every Runnable
  private void sleepInRunnable(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}