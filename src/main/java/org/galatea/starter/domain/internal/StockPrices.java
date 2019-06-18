package org.galatea.starter.domain.internal;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.galatea.starter.domain.modelresponse.ResponsePrices;

@Getter @Setter // Tells Lombok to generate getters/setters for all fields below
@AllArgsConstructor //Tells java to instantiate all fields listed
@Builder
@Entity
public class StockPrices extends ResponsePrices {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;
  private final Date date;
  private final double open;
  private final double high;
  private final double low;
  private final double close;
  private final double adjustedClose;
  private final double volume;
  private final double dividendAmount;
  private final double splitCoefficient;

}