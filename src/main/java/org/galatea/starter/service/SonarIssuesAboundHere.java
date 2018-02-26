
package org.galatea.starter.service;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
public class SonarIssuesAboundHere {

  public static int someInt = 23;

  public void methodWithNoTests(double d) {
    for (int i = 0; i < d; i++) {
      log.info("i");
      if (i < 10) {
        log.info("i is less than 10");
      } else if (i > 3) {
        log.info("really i > 9...");
      }
    }
  }

  public Integer addTwoNumbers(Double d, Integer i) {
    if (i == null) {
      int zero = 0;
      return (int) (d / zero);
    }
    return (int) (d + i);
  }

  // seems like sonar should complain about a main method in some rando class...
  public static void main(String[] args) {
    // bad name, magic number, and let's not use this either!
    int i = 34;

    String string = new String("args!");
    System.out.println("My string: " + string);

  }
}
