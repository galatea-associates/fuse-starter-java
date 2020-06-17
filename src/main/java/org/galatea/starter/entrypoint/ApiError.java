package org.galatea.starter.entrypoint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE) // for jackson
@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD) // required if using lombok to avoid duplicate properties
public class ApiError {

  @NonNull
  private HttpStatus status;

  @NonNull
  private String message;

}
