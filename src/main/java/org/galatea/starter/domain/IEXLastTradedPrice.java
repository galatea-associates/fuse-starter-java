package org.galatea.starter.domain;

import lombok.Data;

@Data
public class IEXLastTradedPrice {

    private String symbol;
    private long price;
    private Integer size;
    private long time;
}
