package org.galatea.starter.utils.jms;

import java.util.function.BiConsumer;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@RequiredArgsConstructor
@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FuseMessageListenerContainer extends DefaultMessageListenerContainer {

  @NonNull
  protected BiConsumer<Message, Exception> failedMessageConsumer;

  @Override
  @SneakyThrows
  protected void invokeListener(final Session session, final Message message) {

    // We expect the listener to handle any retryable exceptions internally. If the exception
    // reaches the catch block, then we assume that the message has failed processing and should
    // NOT be
    // retried. That being said, the failed message consumer could decide to throw a
    // RuntimeException, which would result in the message being placed back on the queue. While
    // this is not encouraged, there may be certain circumstances where that is necessary.
    try {
      super.invokeListener(session, message);
    } catch (JMSException e) {
      failedMessageConsumer.accept(message, e);
    }
  }
}
