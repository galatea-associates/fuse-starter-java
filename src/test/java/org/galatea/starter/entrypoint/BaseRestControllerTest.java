package org.galatea.starter.entrypoint;

import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertEquals;

@RequiredArgsConstructor
@Slf4j
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class BaseRestControllerTest extends BaseRestController {

  @Test
  public void testMessageEncoding(){
    String logInjectionMessage="Log Passed \nSuccess \rSuccess \tLog created";
    String targetEncodedMessage="Log Passed _Success _Success _Log created";
    String encodedMessage = encode(logInjectionMessage);
    assertEquals(targetEncodedMessage, encodedMessage);
  }

}
