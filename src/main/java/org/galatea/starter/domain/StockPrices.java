package org.galatea.starter.domain;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter // Tells Lombok to generate getters/setters for all fields below
@AllArgsConstructor //Tells java to instantiate all fields listed
public class StockPrices {

  private final LocalDate date;
  private final double open;
  private final double high;
  private final double low;
  private final double close;
  private final double adjustedClose;
  private final double volume;
  private final double dividendAmount;
  private final double splitCoefficient;

}
