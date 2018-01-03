package org.galatea.starter.service;

import org.joda.money.BigMoney;

public interface IProceedsCalculator {

  public BigMoney getUSDProceeds(BigMoney base);
}
