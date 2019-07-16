package org.galatea.starter.service.feign;

public class BadRequestException extends Exception {

 public BadRequestException() {
  }

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(Throwable cause) {
    super(cause);
  }

  @Override
  public String toString() {
    return "This is bad: "+getMessage();
  }
}
