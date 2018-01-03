package org.galatea.starter.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.service.client.IFXRestClient;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProceedsCalculator implements IProceedsCalculator {

  @NonNull
  IFXRestClient upstreamService;

  @Override
  public BigMoney getUSDProceeds(BigMoney base) {
    if (base.getCurrencyUnit() == CurrencyUnit.USD) {
      return base;
    }
    return base.convertedTo(CurrencyUnit.USD, getFXRate(base.getCurrencyUnit().getCode()));
  }

  // Potentially a spot to solve these issues:
  // https://github.com/GalateaRaj/fuse-starter-java/issues/24
  // https://github.com/GalateaRaj/fuse-starter-java/issues/38
  private BigDecimal getFXRate(String base) {
    return upstreamService.getRate(base).getExchangeRate();
  }
}
