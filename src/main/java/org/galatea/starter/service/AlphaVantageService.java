package org.galatea.starter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
@Log
@Service
public class AlphaVantageService {
  public String access(String symbol, int days) {
    RestTemplate restTemplate = new RestTemplate();
    String alphaVantageUrl
        = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=";
    String output = days > 100 ? "full":"compact";
    ResponseEntity<String> response
        = restTemplate.getForEntity(alphaVantageUrl + symbol + "&outputsize=" + output + "&apikey={$, String.class);
    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
  }
}
