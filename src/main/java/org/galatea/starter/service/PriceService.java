package org.galatea.starter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Log
@Service
public class PriceService {

  /**
   * Process the stock from GET command into the appropriate command
   *
   * @param stock from the full text from the GET command. Wit.ai will break this down
   * @param daysToLookBack from the full text from the GET command. Wit.ai will break this down
   * @return the result of executing the command with the given parameters
   */

  public String processStock(String stock, Integer daysToLookBack) {

    String parameters = stock + " " + daysToLookBack;
    return parameters;
  }
}
  //place holder for switch based on # of days requested



