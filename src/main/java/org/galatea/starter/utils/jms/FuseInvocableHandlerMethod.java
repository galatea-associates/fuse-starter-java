
package org.galatea.starter.utils.jms;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.utils.Runner;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.lang.reflect.Method;

@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FuseInvocableHandlerMethod extends InvocableHandlerMethod {

  public FuseInvocableHandlerMethod(final Object bean, final Method method) {
    super(bean, method);
  }

  @Override
  protected Object doInvoke(final Object... args) throws Exception {

    return Runner.setThreadAndCall(() -> super.doInvoke(args), Runner.getSuffixFor(args));
  }

}

