package org.galatea.starter.TickerInfo;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class InfoController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping(value = "/info" + "/{ticker}" + "/{days}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Ticker getInfo(@PathVariable final String ticker, @PathVariable final int days) {
        return AlphaVantageService.getTicker(ticker,days);
    }


}