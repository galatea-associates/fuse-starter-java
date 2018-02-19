package org.galatea.starter.domain.exception;

public class SettlementMissionNotFoundException extends RuntimeException {

  public SettlementMissionNotFoundException(Long missionId) {
    super("Settlement Mission " + missionId + " was not found");
  }

}
