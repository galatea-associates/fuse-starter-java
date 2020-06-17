package org.galatea.starter.entrypoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@AllArgsConstructor
public class ApiError {

  @NonNull
  private HttpStatus status;

  @NonNull
  private String message;

}
