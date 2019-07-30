package org.galatea.starter.entrypoint;

import org.galatea.starter.domain.Ticker;
import org.galatea.starter.service.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for retrieving Ticker Information
 */
@RestController
@Controller
public class TickerController {

    @Autowired
    TickerService service;

    /**
     * @param symbol
     * @param days
     * @return Ticker with specified symbol and days
     */
    @GetMapping(value = "/info" + "/{symbol}" + "/{days}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Ticker getInfo(@PathVariable final String symbol, @PathVariable final int days) {
       return service.getTicker(symbol,days);
    }
}