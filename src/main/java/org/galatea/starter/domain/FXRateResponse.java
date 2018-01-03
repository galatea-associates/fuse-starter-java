package org.galatea.starter.domain;

import lombok.*;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FXRateResponse {

    @NonNull
    CurrencyUnit baseCurrency;

    @NonNull
    LocalDate validOn;

    @NonNull
    BigDecimal exchangeRate;

}
