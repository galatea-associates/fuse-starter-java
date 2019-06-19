package org.galatea.starter.domain.internal;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.galatea.starter.domain.modelresponse.ResponsePrices;

@Data// Tells Lombok to generate getters/setters for all fields below
@AllArgsConstructor //Tells java to instantiate all fields listed
@Builder
@Entity
public class StockPrices extends ResponsePrices  {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;
  private   Date date;
  private  double open;
  private  double high;
  private  double low;
  private  double close;
  private  double adjustedClose;
  private  double volume;
  private  double dividendAmount;
  private  double splitCoefficient;
  private  String stock;

  public StockPrices(){

  }
}