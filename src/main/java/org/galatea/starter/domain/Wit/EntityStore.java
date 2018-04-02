package org.galatea.starter.domain.Wit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.galatea.starter.domain.Wit.Entity;

@Data
@EqualsAndHashCode
@ToString
public class EntityStore {
  private Entity[] intent;
  private Entity[] lunch_keywords;
  private Entity[] lunch_descriptors;
  private Entity[] question_descriptors;
  private Entity[] tech;
}

