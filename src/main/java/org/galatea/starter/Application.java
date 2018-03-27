package org.galatea.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


/**
 * This is the entry point for the application.
 *
 */
@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
public class Application {

  /**
   * Start up the spring context. java -Dcapsule.log=verbose -Dcapsule.mode=uat -jar
   * target/fuse-starter-java-0.0.1-SNAPSHOT-capsule.jar
   *
   * @param args command line args
   */
  public static void main(final String[] args) {
    //log.info("Starting spring application {}", System.getProperty("application.name"));
    //SpringApplication.run(Application.class, args);

    //The above code runs the standard application

    //The following code retrieves a quote and prints it to the log.
    ApplicationContext context = SpringApplication.run(Application.class, args);
    QuoteGetter quoteGetter = context.getBean(QuoteGetter.class);
    Quote quote = quoteGetter.getQuote();
    log.info("Quote: " + quote.getQuote() + ", Author: " + quote.getAuthor() + ", Category:" + quote.getCategory());


  }
}
