package org.galatea.starter.domain;

import javax.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class User {

  @Id
  private String id;
  private String firstName;
  private String lastName;
}
