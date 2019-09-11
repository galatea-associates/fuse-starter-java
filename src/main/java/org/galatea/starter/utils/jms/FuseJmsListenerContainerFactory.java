package org.galatea.starter.utils.jms;

import java.util.function.BiConsumer;
import javax.jms.Message;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;


@RequiredArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class FuseJmsListenerContainerFactory extends DefaultJmsListenerContainerFactory {

  @NonNull
  protected BiConsumer<Message, Exception> failedMessageConsumer;

  @Override
  protected DefaultMessageListenerContainer createContainerInstance() {
    return new FuseMessageListenerContainer(failedMessageConsumer);
  }

}
