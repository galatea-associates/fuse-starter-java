package org.galatea.starter.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FxRateResponse {

  @NonNull CurrencyUnit baseCurrency;

  @NonNull LocalDate validOn;

  @NonNull BigDecimal exchangeRate;
}
