package org.galatea.starter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.domain.Quote;
import org.galatea.starter.domain.wit.WitResponse;
import org.galatea.starter.restclient.QuoteGetter;
import org.galatea.starter.restclient.WitGetter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;


/**
 * This is the entry point for the application.
 *
 */
@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
@EnableFeignClients
public class Application {

  /**
   * Start up the spring context. java -Dcapsule.log=verbose -Dcapsule.mode=uat -jar
   * target/fuse-starter-java-0.0.1-SNAPSHOT-capsule.jar
   *
   * @param args command line args
   */
  public static void main(final String[] args) {

    log.info("Starting spring application {}", System.getProperty("application.name"));
    SpringApplication.run(Application.class, args);


    /*
    ApplicationContext context = SpringApplication.run(Application.class, args);

    System.out.println("here");
    QuoteGetter quoteGetter = context.getBean(QuoteGetter.class);
    System.out.println("here2");

    Quote[] quote = quoteGetter.getQuote();
    System.out.println("here3");

    System.out.println(quote[0].getQuoteText() + ", " + quote[0].getAuthor());

    WitGetter witGetter = context.getBean(WitGetter.class);
    System.out.println("here9999");

    WitResponse witResponse = witGetter.getWitResponse("Bearer KGPXCMYTIUAJAWE7R4IVBBL7OTE7L7UE", "flip a coin");

    System.out.println(witResponse.getText());
  */


  }
}
