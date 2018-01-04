package org.galatea.starter.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.joda.money.BigMoney;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/* For builder since we explictly want to make the all args ctor private */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Slf4j
@Entity
public class SettlementMission {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @NonNull protected String instrument;

  @NonNull protected String externalParty;

  @NonNull protected String depot;

  @NonNull protected String direction;

  @NonNull protected Double qty;

  @NonNull protected BigMoney proceeds;

  @NonNull protected BigMoney usdProceeds;
}
