
package org.galatea.starter;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.utils.FuseTraceRepository;
import org.galatea.starter.utils.rest.FuseWebRequestTraceFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.trace.TraceProperties;
import org.springframework.boot.actuate.trace.WebRequestTraceFilter;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@Slf4j
public class MvcConfig extends WebMvcConfigurerAdapter {

  @Autowired
  protected TraceProperties traceProperties;

  @Autowired
  protected ObjectProvider<ErrorAttributes> errorAttributesProvider;

  @Value("${mvc.max-size-trace-payload}")
  protected int maxTracePayloadSize;

  /**
   * This is used to trace web requests and store that trace info.
   *
   * @return the trace filter
   */
  @Bean
  public WebRequestTraceFilter webRequestLoggingFilter() {

    // Trace everything!
    traceProperties.setInclude(Sets.newHashSet(TraceProperties.Include.values()));

    WebRequestTraceFilter filter = new FuseWebRequestTraceFilter(traceRepository(),
        this.traceProperties, path -> path.startsWith("/trace"), maxTracePayloadSize);

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

}
