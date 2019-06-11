
package org.galatea.starter.domain.ModelResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.Map;
import lombok.Data;

@Data
@SuppressWarnings("unused")
public class TimeSeriesDaily {

    @JsonProperty ("Time Series (Daily)")
    private Map<Date, Prices> dates;

}
