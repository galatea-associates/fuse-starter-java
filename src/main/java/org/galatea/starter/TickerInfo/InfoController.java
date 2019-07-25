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
        Ticker info = AlphaVantageService.getTicker(ticker,days);
        trimTicker(info,days);

        return info;
    }


    private void trimTicker(Ticker ticker, int days){
        HashMap<String, Day> timeSeries = new HashMap<String,Day>();

        for(int i = 0; i<days; i++) {
            if(ticker.timeSeries.get(AlphaVantageService.getDate(i)) != null)
                timeSeries.put(AlphaVantageService.getDate(i), ticker.timeSeries.get(AlphaVantageService.getDate(i)));
        }
        ticker.setTimeSeries(timeSeries);




    }


}