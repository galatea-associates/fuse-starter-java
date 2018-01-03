package org.galatea.starter.service;

import org.joda.money.BigMoney;

public interface IProceedsCalculator {

  BigMoney getUsdProceeds(BigMoney base);
}
