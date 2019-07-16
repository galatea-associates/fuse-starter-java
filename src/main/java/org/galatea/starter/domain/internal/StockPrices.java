package org.galatea.starter.domain.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockPrices {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private  Date date;
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