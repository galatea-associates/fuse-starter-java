
package org.galatea.starter.domain.ModelResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.HashMap;
import lombok.Data;

@Data
@SuppressWarnings("unused")

public class AlphaPrices {

    @JsonProperty ("Time Series (Daily)")
    public HashMap<Date, ResponsePrices> avPrices;


}
