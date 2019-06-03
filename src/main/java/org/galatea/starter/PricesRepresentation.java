package org.galatea.starter;

public class PricesRepresentation {

  //specify fields
  private final int date;
  private final double open;
  private final double high;
  private final double low;
  private final double close;
  private final double adjustedClose;
  private final double volume;
  private final double dividendAmount;
  private final double splitCoefficient;

//  metadata
//  private final String timeStamp;
//  private final String responseTime;
//  private final String endpoint;
//  private final String host;


  //specify constructor object
  public PricesRepresentation (int date, double open, double high, double low, double close, double adjustedClose,
      double volume, double dividendAmount, double splitCoefficient){
    this.date = date;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.adjustedClose = adjustedClose;
    this.volume = volume;
    this.dividendAmount = dividendAmount;
    this.splitCoefficient = splitCoefficient;

  }
  //specify accessors for ID and content data
  public int getdate(){
    return date;
  }
  public double getOpen(){
    return open;
  }
  public double getHigh(){
    return high;
  }
  public double getLow(){
    return low;
  }
  public double getClose(){
    return close;
  }
  public double getadjustedClose(){
    return adjustedClose;
  }
  public double getVolume(){
    return volume;
  }
  public double getDividendAmount(){
    return dividendAmount;
  }
  public double getSplitCoefficient(){
    return splitCoefficient;
  }
}
