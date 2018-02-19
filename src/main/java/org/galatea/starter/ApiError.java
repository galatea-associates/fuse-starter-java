package org.galatea.starter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Data
@AllArgsConstructor
@ToString
public class ApiError {

  @NonNull
  private HttpStatus status;

  @NonNull
  private String message;

}
