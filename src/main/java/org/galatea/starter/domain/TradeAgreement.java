package org.galatea.starter.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.galatea.starter.utils.deserializers.TradeAgreementDeserializer;
import org.joda.money.BigMoney;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@JsonDeserialize(using = TradeAgreementDeserializer.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // For builder
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For spring and jackson
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
public class TradeAgreement {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @NotNull
  protected String instrument;

  @NotNull
  protected String internalParty;

  @NotNull
  protected String externalParty;

  @NotNull
  protected String buySell;

  @NotNull
  protected Double qty;

  @NotNull
  protected BigMoney proceeds;
}
