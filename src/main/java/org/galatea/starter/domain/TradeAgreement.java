package org.galatea.starter.domain;

import javax.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.utils.validation.StringEnumeration;

@AllArgsConstructor(access = AccessLevel.PRIVATE) // For builder
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
@Builder
@Data
@Slf4j
public class TradeAgreement {

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
