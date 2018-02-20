package org.galatea.starter.service;

import org.galatea.starter.ApiError;
import org.galatea.starter.domain.exception.SettlementMissionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *  A centralized REST handler that intercepts defined exceptions thrown by controller calls,
 *  enabling a custom ResponseEntity to be returned.
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handles SettlementMissionNotFoundException's thrown by calls to any web controller,
   * setting an appropriate response body containing the HttpStatus and exception message.
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
