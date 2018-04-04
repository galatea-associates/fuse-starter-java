package org.galatea.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.domain.Quote;
import org.galatea.starter.domain.Wit.WitResponse;
import org.galatea.starter.restClient.QuoteGetter;
import org.galatea.starter.restClient.WitGetter;
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


    //Comment out above code and uncomment below to run an example Wit request and print to command line

    ApplicationContext context = SpringApplication.run(Application.class, args);
    WitGetter witGetter = context.getBean(WitGetter.class);
    WitResponse witResponse = witGetter.getWitResponse("flip a coin");
    System.out.println(witResponse.getEntities().getIntent()[0]);

  }
}
