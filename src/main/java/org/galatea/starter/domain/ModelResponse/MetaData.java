
package org.galatea.starter.domain.ModelResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetaData {

    @JsonProperty("1. Information")
    private String information;
    @JsonProperty("2. Symbol")
    private String symbol;
    @JsonProperty("3. Output Size")
    private String timezone;
    @JsonProperty("4. Last Refreshed")
    private String lastRefreshed;
    @JsonProperty("5. Time Zone")
    private String timeZone;

    public static final String META_DATA_RESPONSE_KEY = "Meta Data";
}
