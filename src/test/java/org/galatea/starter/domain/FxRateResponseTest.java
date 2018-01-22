package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;

import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class FxRateResponseTest {

  Validator validator;
  FxRateResponse response;

  @Before
  public void setUp() {
    ValidatorFactory factory = Validation.byDefaultProvider()
        .configure()
        .buildValidatorFactory();
    validator = factory.getValidator();

    response = FxRateResponse.builder()
        .baseCurrency(CurrencyUnit.GBP)
        .validOn(LocalDate.of(2018, 1, 19))
        .exchangeRate(BigDecimal.valueOf(1))
        .build();
  }

  @Test
  public void testValidation() {
    Set<ConstraintViolation<FxRateResponse>> constraintViolations = validator.validate(response);
    assertEquals(0, constraintViolations.size());
  }

}