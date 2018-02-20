package org.galatea.starter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiError {

  @NonNull
  private HttpStatus status;

  @NonNull
  private String message;

}
