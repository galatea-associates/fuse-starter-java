package org.galatea.starter.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Settlement Mission was not found")
public class SettlementMissionNotFoundException extends RuntimeException {

  public SettlementMissionNotFoundException(Long missionId) {
    super("Settlement Mission " + missionId + " was not found");
  }

}
