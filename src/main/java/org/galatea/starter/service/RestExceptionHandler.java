package org.galatea.starter.service;

import org.galatea.starter.ApiError;
import org.galatea.starter.domain.exception.SettlementMissionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *  REST handler that intercepts exceptions thrown by controller calls
 *  and returns an appropriate response that is serialized into JSON format.
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handle SettlementMissionNotFoundException thrown by all Spring web controllers
   */
  @ExceptionHandler(SettlementMissionNotFoundException.class)
  protected ResponseEntity<Object> handleSettlementMissionNotFound(
      SettlementMissionNotFoundException ex) {
    ApiError error = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
    return buildResponseEntity(error);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

}
