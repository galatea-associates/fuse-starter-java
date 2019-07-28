package org.galatea.starter.domain;

import lombok.Data;

import java.util.Date;

@Data
public class IEXStockSymbol {

    private String symbol;
    private String name;
    private Date date;
    private boolean isEnabled;
    private String type;
    private String iexId;

}
