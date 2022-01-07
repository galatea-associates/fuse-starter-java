package org.galatea.starter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// see https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api section 4.2 for
// configuring w/o Spring Boot
public class SwaggerConfig implements WebMvcConfigurer {

  // the documentation says this shouldn't be necessary, but swagger-ui.html wasn't available
  // without it...
  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

}
