package org.galatea.starter.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.FXRateResponse;
import org.galatea.starter.service.client.IFXRestClient;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProceedsCalculator implements IProceedsCalculator {

    @NonNull
    IFXRestClient upstreamService;

    @Override
    public BigMoney getUSDProceeds(BigMoney base) {
        return base.convertedTo(CurrencyUnit.USD, getFXRate(base.getCurrencyUnit().getCode()));
    }

    private BigDecimal getFXRate(String base){
        FXRateResponse response = upstreamService.rate("GBP");
        log.info("FXRateResponse: " + response.toString());
        return response.getExchangeRate();
    }
}
