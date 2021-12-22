package org.galatea.starter;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.utils.exception.MissingOptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;


/**
 * This is the entry point for the application.
 */
@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
public class Application implements ApplicationRunner {

  /**
   * Start up the spring context.
   *
   * @param args command line args
   */
  public static void main(final String[] args) {
    log.info("Starting spring application {}", System.getProperty("application.name"));
    SpringApplication.run(Application.class, args);
    String a = args[1];
    log.info("Started successfully, you can view swagger UI here: {}:{}/",
        InetAddress.getLoopbackAddress().getHostName(),
        a.substring(14,18));
  }

  /**
   * Ensure that server port is passed in as a command line argument.
   *
   * @param args command line arguments
   * @throws MissingOptionException if server port not provided as argument
   */
  @Override
  public void run(final ApplicationArguments args) {
    if (!args.containsOption("server.port") && System.getProperty("server.port") == null) {
      throw new MissingOptionException("Server port must be set via command line parameter");
    }
  }
}
