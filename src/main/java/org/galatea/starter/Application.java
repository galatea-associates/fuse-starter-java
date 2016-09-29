package org.galatea.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


/**
 * This is the entry point for the application.
 *
 */
@SpringBootApplication
public class Application {

  /**
   * Start up the spring context.
   *
   * @param args command line args
   */
  public static void main(final String[] args) {

    ApplicationContext ctx = SpringApplication.run(Application.class, args);

  }
}
