package org.galatea.starter;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

public class LogMeterRegistry extends StepMeterRegistry {
  private final Logger logger = LoggerFactory.getLogger(LogMeterRegistry.class);

  /**
   * @param step Governs on what frequency metrics are logged
   */
  public LogMeterRegistry(Duration step) {
    super(new StepRegistryConfig() {
      @Override
      public String prefix() {
        return "log";
      }

      @Override
      public String get(String key) {
        return null;
      }

      @Override
      public Duration step() {
        return step;
      }
    }, Clock.SYSTEM);
  }
  @Override
  protected void publish() {
    for (Meter meter : getMeters()) {
      logger.info(meter.getId().toString());
      for (Measurement measurement : meter.measure()) {
        logger.info(measurement.getStatistic().toString() + "=" + measurement.getValue());
      }

    }
  }

  @Override
  protected TimeUnit getBaseTimeUnit() {
    return TimeUnit.SECONDS;
  }
}
