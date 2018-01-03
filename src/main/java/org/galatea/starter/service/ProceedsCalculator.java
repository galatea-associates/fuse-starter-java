package org.galatea.starter.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.service.client.IFxRestClient;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProceedsCalculator implements IProceedsCalculator {

  @NonNull
  IFxRestClient upstreamService;

  @Override
  public BigMoney getUsdProceeds(BigMoney base) {
    if (base.getCurrencyUnit() == CurrencyUnit.USD) {
      return base;
    }
    return base.convertedTo(CurrencyUnit.USD, getFxRate(base.getCurrencyUnit().getCode()));
  }

  // Potentially a spot to solve these issues:
  // https://github.com/GalateaRaj/fuse-starter-java/issues/24
  // https://github.com/GalateaRaj/fuse-starter-java/issues/38
  private BigDecimal getFxRate(String base) {
    return upstreamService.getRate(base).getExchangeRate();
  }
}
