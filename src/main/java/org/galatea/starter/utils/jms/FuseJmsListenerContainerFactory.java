
package org.galatea.starter.utils.jms;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.utils.FuseTraceRepository;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import java.util.function.BiConsumer;

import javax.jms.Message;


@RequiredArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class FuseJmsListenerContainerFactory extends DefaultJmsListenerContainerFactory {

  @NonNull
  protected FuseTraceRepository repository;

  @NonNull
  protected BiConsumer<Message, Exception> failedMessageConsumer;

  @Override
  protected DefaultMessageListenerContainer createContainerInstance() {
    return new FuseMessageListenerContainer(repository, failedMessageConsumer);
  }

}
