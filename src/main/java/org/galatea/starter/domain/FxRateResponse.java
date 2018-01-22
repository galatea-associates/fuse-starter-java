package org.galatea.starter.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@ToString
public class FxRateResponse {

  @NotNull
  CurrencyUnit baseCurrency;

  @NotNull()
  LocalDate validOn;

  @NotNull
  BigDecimal exchangeRate;
}
