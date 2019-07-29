package org.galatea.starter.domain;

import java.util.Date;
import lombok.Data;

@Data
public class IexStockSymbol {

  private String symbol;
  private String name;
  private Date date;
  private boolean isEnabled;
  private String type;
  private String iexId;

}
