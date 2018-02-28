package org.galatea.starter.service;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.ApiError;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A centralized REST handler that intercepts exceptions thrown by controller calls, enabling a
 * custom response to be returned.
 *
 * We can use this to handle predefined exceptions that we cannot annotate with @ResponseStatus.
 */
@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

  @ExceptionHandler(DataAccessException.class)
  protected ResponseEntity<Object> handleDataAccessException(final DataAccessException exception) {
    log.error("Unexpected data access error", exception);

    ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, getStackTrace(exception));
    return buildResponseEntity(error);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

}
