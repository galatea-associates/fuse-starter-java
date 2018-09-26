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

    static final String TARGET_ENCODED_MESSAGE = "Log Passed_Log created";

  @Test
  public void testMessageEncodingNewLine(){
    String logInjectionMessage="Log Passed\nLog created";
    String encodedMessage = encode(logInjectionMessage);
    assertEquals(TARGET_ENCODED_MESSAGE, encodedMessage);
  }

  @Test
  public void testMessageEncodingCarriageReturn(){
    String logInjectionMessage="Log Passed\rLog created";
    String encodedMessage = encode(logInjectionMessage);
    assertEquals(TARGET_ENCODED_MESSAGE, encodedMessage);
  }

  @Test
  public void testMessageEncodingTab(){
    String logInjectionMessage="Log Passed\tLog created";
    String encodedMessage = encode(logInjectionMessage);
    assertEquals(TARGET_ENCODED_MESSAGE, encodedMessage);
  }

}
