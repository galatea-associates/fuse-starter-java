package org.galatea.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.TextMessage;


/**
 * This is the entry point for the application.
 *
 */
@SpringBootApplication
public class Application {

  /**
   * Start up the spring context. java -Dcapsule.log=verbose -Dcapsule.mode=uat -jar
   * target/fuse-starter-java-0.0.1-SNAPSHOT-capsule.jar
   *
   * @param args command line args
   */
  public static void main(final String[] args) {

    ApplicationContext ctx = SpringApplication.run(Application.class, args);

    // Sends a test message through the system when you start it up.
    // TODO: Remove this
    JmsTemplate template = ctx.getBean(JmsTemplate.class);
    template.send("sandbox.agreement", s -> {
      TextMessage msg = s.createTextMessage("{" + "\"instrument\": \"IBM\","
          + "\"_type\": \"TradeAgreement\"," + "\"internalParty\": \"INT-1\","
          + "\"externalParty\": \"EXT-1\"," + "\"buySell\": \"B\"," + "\"qty\": 100" + "}");
      return msg;
    });


  }
}
