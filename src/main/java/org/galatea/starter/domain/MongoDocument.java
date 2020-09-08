package org.galatea.starter.domain;

import java.util.Date;
import lombok.Data;

@Data public class MongoDocument {
  private Date date;
  private Double open;
  private Double high;
  private Double low;
  private Double close;
  private Integer volume;
}
