
package org.galatea.starter.utils.jms;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.StopWatch;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;


@RequiredArgsConstructor
@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FuseMessageListenerContainer extends DefaultMessageListenerContainer {

  @NonNull
  protected Integer maxRetryCount;

  @NonNull
  protected BiConsumer<Session, Message> messageAuditor;

  @NonNull
  protected BiConsumer<Message, Exception> failedMessageConsumer;

  @NonNull
  protected Predicate<Exception> isTransientClassifier;

  @Override
  protected void invokeListener(final Session session, final Message message) throws JMSException {
    StopWatch sw = new StopWatch();
    sw.start();

    try {
      messageAuditor.accept(session, message);
    } catch (RuntimeException rte) {
      log.error("Error calling message auditor. Continuing to process message.", rte);
    }

    boolean msgErrored = false;
    try {
      super.invokeListener(session, message);
    } catch (Exception err) {
      msgErrored = true;
      int retryCnt = message.getIntProperty("JMSXDeliveryCount");

      if (isTransientClassifier.test(err) && retryCnt <= maxRetryCount) {
        log.info("Transient error. Propagate exception and retry. Retry count {}", retryCnt);
        throw new RuntimeException(err);
      } else {
        log.error("Message failed processing.  Sending to failed message consumer", err);
        failedMessageConsumer.accept(message, err);
      }

    } finally {
      sw.stop();
      log.info("Message " + (msgErrored ? "errored" : "processed") + " in {}(ms)",
          sw.getTotalTimeMillis());
    }
  }
}
