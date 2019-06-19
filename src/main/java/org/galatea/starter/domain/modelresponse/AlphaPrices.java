
package org.galatea.starter.domain.modelresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.HashMap;
import lombok.Data;

@Data
@SuppressWarnings("unused")
public class AlphaPrices {

    @JsonProperty ("Time Series (Daily)")
    private HashMap<Date, ResponsePrices> avPrices;

}
