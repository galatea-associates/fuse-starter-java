package org.galatea.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.utils.exception.MissingOptionException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;


/**
 * This is the entry point for the application.
 *
 */
@RequiredArgsConstructor
@SpringBootApplication
@Slf4j
@EnableFeignClients
@Log
public class Application implements ApplicationRunner {

  /**
   * Start up the spring context.
   *
   * @param args command line args
   */
  public static void main(final String[] args) {

    long startTime = System.currentTimeMillis();
    log.info("Starting spring application {}", System.getProperty("application.name"));
    SpringApplication.run(Application.class, args);

    long endTime = System.currentTimeMillis();
    long processTime = endTime - startTime;
    log.info ("System start up time (ms): {}", (endTime - startTime));
  }

  /**
   * Ensure that server port is passed in as a command line argument.
   *
   * @param args command line arguments
   * @throws MissingOptionException if server port not provided as argument
   */

  @Override
  public void run(ApplicationArguments args) {

    if (!args.containsOption("server.port") && System.getProperty("server.port") == null) {
      throw new MissingOptionException("Server port must be set via command line parameter");
    }
  }


}