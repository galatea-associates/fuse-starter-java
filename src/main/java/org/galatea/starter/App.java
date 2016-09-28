package org.galatea.starter;
import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App {
  
  public static void main(String[] args) {
    System.out.println("Hello World!");
    System.out.println(System.getProperty("herp-drop"));
    System.out.println(Runtime.getRuntime().maxMemory());

    ApplicationContext ctx = SpringApplication.run(App.class, args);

    System.out.println("Let's inspect the beans provided by Spring Boot:");

    String[] beanNames = ctx.getBeanDefinitionNames();
    Arrays.sort(beanNames);
    for (String beanName : beanNames) {
        System.out.println(beanName);
    }
}
}
