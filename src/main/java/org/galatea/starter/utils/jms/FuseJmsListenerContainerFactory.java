
package org.galatea.starter.utils.jms;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.jms.Message;
import javax.jms.Session;


@RequiredArgsConstructor
@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FuseJmsListenerContainerFactory extends DefaultJmsListenerContainerFactory {

  @NonNull
  protected Integer maxRetryCount;

  @NonNull
  protected BiConsumer<Session, Message> messageAuditor;

  @NonNull
  protected BiConsumer<Message, Exception> failedMessageConsumer;

  @NonNull
  protected Predicate<Exception> isTransientClassifier;

  @Override
  protected DefaultMessageListenerContainer createContainerInstance() {
    return new FuseMessageListenerContainer(maxRetryCount, messageAuditor, failedMessageConsumer,
        isTransientClassifier);
  }

}
