package org.galatea.starter.service.feign;

public class RedirectException extends Exception {

    public RedirectException() {
    }

    public RedirectException(String message) {
      super(message);
    }

    public RedirectException(Throwable cause) {
      super(cause);
    }

    @Override
    public String toString() {
      return "RedirectRequestException: "+getMessage();
    }
}
