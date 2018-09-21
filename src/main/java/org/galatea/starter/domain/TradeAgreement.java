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

@AllArgsConstructor(access = AccessLevel.PRIVATE) // For builder
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
@Builder
@Data
@Slf4j
@Entity
public class TradeAgreement {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @NonNull
  protected String instrument;

  @NonNull
  protected String internalParty;

  @NonNull
  protected String externalParty;

  @StringEnumeration(enumClass = Side.class, message = "Buy/Sell side must be valid")
  @NonNull
  protected String buySell;

  @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
  @NonNull
  protected Double qty;

}
