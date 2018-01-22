package org.galatea.starter.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class FxRateResponse {

  @NotNull
  CurrencyUnit baseCurrency;

  @NotNull()
  LocalDate validOn;

  @NotNull
  BigDecimal exchangeRate;

  @lombok.Generated
  public String toString() {
    return "FxRateResponse(baseCurrency=" + this.getBaseCurrency() + ", validOn=" + this
        .getValidOn() + ", exchangeRate=" + this.getExchangeRate() + ")";
  }
}
