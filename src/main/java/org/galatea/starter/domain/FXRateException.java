package org.galatea.starter.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// The error handling for feign is currently pretty dirty.
// The client ends up with a FeignException rather than this nice exception.
// A better way might be a custom error decoder:
// https://github.com/OpenFeign/feign/wiki/Custom-error-handling
@ResponseStatus(value= HttpStatus.SERVICE_UNAVAILABLE)
public class FXRateException extends RuntimeException {

    public FXRateException(String message) {
        super(message);
    }
}
