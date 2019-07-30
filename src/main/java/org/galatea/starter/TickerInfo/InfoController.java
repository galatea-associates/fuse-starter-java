package org.galatea.starter.TickerInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.SortedMap;

@RestController
@Controller
public class InfoController {

    @Autowired
    TickerInfoService service;

    @GetMapping(value = "/info" + "/{symbol}" + "/{days}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Ticker getInfo(@PathVariable final String symbol, @PathVariable final int days) {
       return service.getTicker(symbol,days);
    }





}