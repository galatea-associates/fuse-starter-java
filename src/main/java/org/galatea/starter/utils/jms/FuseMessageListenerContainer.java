package org.galatea.starter.utils.jms;

import static org.galatea.starter.utils.Tracer.addTraceInfo;
import static org.galatea.starter.utils.Tracer.setExternalRequestId;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.utils.FuseTraceRepository;
import org.galatea.starter.utils.Tracer.AutoClosedTrace;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import java.util.function.BiConsumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

@RequiredArgsConstructor
@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FuseMessageListenerContainer extends DefaultMessageListenerContainer {

  @NonNull protected FuseTraceRepository repository;

  @NonNull protected BiConsumer<Message, Exception> failedMessageConsumer;

  public static final String UNK = "UNKNOWN";

  @Override
  @SneakyThrows
  protected void invokeListener(final Session session, final Message message) throws JMSException {

    try (AutoClosedTrace t = new AutoClosedTrace(repository, this.getClass())) {

      addMessageInfoToTracer(message);

      // We expect the listener to handle any retryable exceptions internally. If the exception
      // reaches the catch block, then we assume that the message has failed processing and should
      // NOT be
      // retried. That being said, the failed message consumer could decide to throw a
      // RuntimeException, which would result in the message being placed back on the queue. While
      // this is not encouraged, there may be certain circumstances where that is necessary.
      try {
        t.runAndTraceSuccess(
            "message",
            () -> {
              super.invokeListener(session, message);
              return Void.TYPE;
            });
      } catch (Exception e) {
        failedMessageConsumer.accept(message, e);
      }
    }
  }

  protected void addMessageInfoToTracer(final Message msg) {
    String dest = UNK;
    String text = UNK;
    String msgId = UNK;
    long msgTs = -1;
    try {
      dest = msg.getJMSDestination().toString();
      msgId = msg.getJMSMessageID();
      msgTs = msg.getJMSTimestamp();

      if (msg instanceof TextMessage) {
        text = ((TextMessage) msg).getText();
      }
    } catch (JMSException jmse) {
      log.error("Could not extract useful info from message.  Using unknown for tracer", jmse);
    }

    addTraceInfo(this.getClass(), "jms-destination", dest);
    addTraceInfo(this.getClass(), "jms-messageId", msgId);
    addTraceInfo(this.getClass(), "jms-messageTs", msgTs);
    addTraceInfo(this.getClass(), "jms-payload", text);

    setExternalRequestId(msgId);
  }
}
