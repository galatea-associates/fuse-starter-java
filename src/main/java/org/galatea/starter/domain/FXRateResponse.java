package org.galatea.starter.domain;

import lombok.*;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@ToString
@RequiredArgsConstructor
public class FXRateResponse {

    @NonNull
    CurrencyUnit baseCurrency;

    @NonNull
    Date validOn;

    @NonNull
    BigDecimal exchangeRate;

}
