package org.galatea.starter.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class TradeAgreementException extends RuntimeException {

    public TradeAgreementException(String message) {
        super(message);
    }
}
