package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class SettlementMissionTest {

  private static Validator validator;

  @BeforeClass
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void qtyMustBePositive() {
    SettlementMission mission = SettlementMission.builder()
        .instrument("I")
        .externalParty("ECP")
        .direction("BUY")
        .depot("DTC")
        .qty(0d).build();

    Set<ConstraintViolation<SettlementMission>> constraintViolations = validator
        .validate(mission);

    assertEquals("must be greater than 0.0", constraintViolations.iterator().next().getMessage());
    assertEquals(1, constraintViolations.size());
  }

}