
package org.galatea.starter.service;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
public class NoTestCoverage {

  public void doSomething(String inputString) {
    log.info("The input string was: {}", inputString);
    for (int i = 0; i < inputString.length(); i++) {
      log.info("Char at point {} is {}", i, inputString.charAt(i));
    }
  }

}
