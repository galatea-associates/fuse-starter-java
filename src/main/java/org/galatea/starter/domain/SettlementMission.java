package org.galatea.starter.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.utils.validation.StringEnumeration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;

/* For builder since we explicitly want to make the all args ctor private */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
@Builder
@Data
@Slf4j
@Entity
public class SettlementMission {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @NonNull
  protected String instrument;

  @NonNull
  protected String externalParty;

  @NonNull
  protected String depot;

  @StringEnumeration(enumClass = Direction.class, message = "Direction must be valid")
  @NonNull
  protected String direction;

  @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
  @NonNull
  protected Double qty;

}
