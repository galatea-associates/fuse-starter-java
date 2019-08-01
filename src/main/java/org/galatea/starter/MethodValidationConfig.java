package org.galatea.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * Config for method-level validation (such as validating method input params).
 */
@Configuration
@ComponentScan("org.galatea.starter")
public class MethodValidationConfig {

  /**
   * Delegates the Bean Validation to a provider (in our case, the Hibernate Validator).
   */
  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    return new MethodValidationPostProcessor();
  }
}
