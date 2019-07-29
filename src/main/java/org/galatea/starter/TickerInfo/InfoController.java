package org.galatea.starter.TickerInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.SortedMap;

@RestController
public class InfoController {

    @GetMapping(value = "/info" + "/{ticker}" + "/{days}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Ticker getInfo(@PathVariable final String ticker, @PathVariable final int days) {
        TickerInfoRepository repo = new TickerInfoRepository();
        return repo.getTicker(ticker,days);


    }





}