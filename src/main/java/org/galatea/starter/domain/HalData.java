package org.galatea.starter.domain;

import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/* For builder since we explicitly want to make the all args ctor private */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
@Builder
@Data
@Entity
public class HalData {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @NonNull
  private String user;

  @NonNull
  private String intent;

  @NonNull
  @ElementCollection
  private List<String> entities;

//  @NonNull
//  private Map<String, List<WitAiTrait>> traits;

  @NonNull
  private String data;

  // -----------------------------------------------
  // Hal: show me a movie quote
  // -----------------------------------------------
  // id: 123
  // intent: movieQuote
  // entities: []
  // data: I'll be back

  // -----------------------------------------------
  // Hal: metWith
  // -----------------------------------------------

  // id: 234
  // intent: metWith
  // entities: Bhavesh
  // data: wants us to build a whole new system from the ground up

  // /metWith bhavesh wants us to build a whole new system from the ground up

  // -----------------------------------------------
  // Hal: show me vijay meetings
  // -----------------------------------------------

  // id: 234
  // intent: showMeMeetings
  // entities: Vijay
  // data: wants us to build a whole new system from the ground up

  // -----------------------------------------------
  // Hal: lessons learned
  // -----------------------------------------------

  // id: 345
  // intent: lessonsLearned
  // entities: spring
  // data: setter injection can lead to inaccurately set up tests, which can lead to tests
  //        passing when they shouldn't

}
