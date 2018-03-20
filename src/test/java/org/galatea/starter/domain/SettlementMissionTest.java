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
  public void validSettlementMission() {
    SettlementMission mission = SettlementMission.builder()
        .instrument("I")
        .externalParty("ECP")
        .direction("DEL")
        .depot("DTC")
        .qty(10D).build();

    Set<ConstraintViolation<SettlementMission>> constraintViolations = validator.validate(mission);

    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void qtyMustBePositive() {
    double invalidQty = 0d;

    SettlementMission mission = SettlementMission.builder()
        .instrument("I")
        .externalParty("ECP")
        .direction("DEL")
        .depot("DTC")
        .qty(invalidQty).build();

    Set<ConstraintViolation<SettlementMission>> constraintViolations = validator
        .validate(mission);

    assertEquals("Quantity must be greater than 0",
        constraintViolations.iterator().next().getMessage());
    assertEquals(1, constraintViolations.size());
  }

  @Test
  public void directionMustBeValid() {
    String invalidDirection = "unknown";

    SettlementMission mission = SettlementMission.builder()
        .instrument("I")
        .externalParty("ECP")
        .direction(invalidDirection)
        .depot("DTC")
        .qty(1d).build();

    Set<ConstraintViolation<SettlementMission>> constraintViolations = validator
        .validate(mission);

    assertEquals("Direction must be valid", constraintViolations.iterator().next().getMessage());
    assertEquals(1, constraintViolations.size());
  }

}