
package org.galatea.starter.utils.jms;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolverComposite;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


@RequiredArgsConstructor
@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FuseMessageHandlerMethodFactory extends DefaultMessageHandlerMethodFactory {

  @Override
  public InvocableHandlerMethod createInvocableHandlerMethod(final Object bean,
      final Method method) {

    InvocableHandlerMethod handlerMethod = new FuseInvocableHandlerMethod(bean, method);

    // Sorry spring, but you leave me no choice...
    Field f =
        ReflectionUtils.findField(DefaultMessageHandlerMethodFactory.class, "argumentResolvers");
    ReflectionUtils.makeAccessible(f);;

    handlerMethod.setMessageMethodArgumentResolvers(
        (HandlerMethodArgumentResolverComposite) ReflectionUtils.getField(f, this));

    return handlerMethod;
  }
}
