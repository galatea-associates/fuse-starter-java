
package org.galatea.starter;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.Messages;
import org.galatea.starter.utils.FuseTraceRepository;
import org.galatea.starter.utils.rest.FuseWebRequestTraceFilter;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.trace.TraceProperties;
import org.springframework.boot.actuate.trace.WebRequestTraceFilter;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Slf4j
@Configuration
@EnableWebMvc
@EnableConfigurationProperties(TraceProperties.class)
public class MvcConfig extends WebMvcConfigurerAdapter {

  /**
   * This is used to trace web requests and store that trace info.
   *
   * @return the trace filter
   */
  @Bean
  public WebRequestTraceFilter webRequestLoggingFilter(final TraceProperties traceProperties,
      final ObjectProvider<ErrorAttributes> errorAttributesProvider,
      @Value("${mvc.max-size-trace-payload}") final int maxTracePayloadSize) {

    // Trace everything!
    traceProperties.setInclude(Sets.newHashSet(TraceProperties.Include.values()));

    WebRequestTraceFilter filter = new FuseWebRequestTraceFilter(traceRepository(), traceProperties,
        path -> path.startsWith("/trace"), maxTracePayloadSize);

    ErrorAttributes errorAttributes = errorAttributesProvider.getIfAvailable();
    if (errorAttributes != null) {
      filter.setErrorAttributes(errorAttributes);
    }

    return filter;
  }

  @Bean
  public FuseTraceRepository traceRepository() {
    return new FuseTraceRepository();
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.favorParameter(true) // give precedence to url request parameters
        .ignoreAcceptHeader(false) // enable use of the Accept header for content negotiation
        .useJaf(false) // let's not fallback on the Java Activation Framework
        .defaultContentType(MediaType.APPLICATION_JSON);
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    // The Protobuf converter MUST be added first, otherwise Jackson will try and handle our
    // protobuf to JSON conversion (and will of course, fail).
    converters.add(new ProtobufHttpMessageConverter()); // Protobuf, XML & JSON supported
    converters.add(new MappingJackson2HttpMessageConverter()); // JSON
    converters.add(new Jaxb2RootElementHttpMessageConverter()); // XML
    super.configureMessageConverters(converters);
  }

  @Bean
  public ITranslator<SettlementMission, Messages.SettlementMissionMessage> settlementMissionTranslator() {
    return mission -> Messages.SettlementMissionMessage.newBuilder()
        .setId(mission.getId())
        .setInstrument(mission.getInstrument())
        .setExternalParty(mission.getExternalParty())
        .setDirection(mission.getDirection())
        .setDepot(mission.getDepot())
        .setQty(mission.getQty()).build();
  }

  @Bean
  public ITranslator<Messages.TradeAgreementMessage, TradeAgreement> tradeAgreementMessageTranslator() {
    return message -> TradeAgreement.builder()
        .id(message.getId())
        .buySell(message.getBuySell())
        .externalParty(message.getExternalParty())
        .instrument(message.getInstrument())
        .internalParty(message.getInternalParty())
        .qty(message.getQty()).build();
  }

}
