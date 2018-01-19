package org.galatea.starter.utils.deserializers;

import static org.galatea.starter.TestUtilities.getJsonFromFile;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.domain.TradeAgreement;
import org.joda.money.BigMoney;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Slf4j
public class TradeAgreementDeserializerTest {

  private TradeAgreementDeserializer deserializer;
  private ObjectMapper mapper;
  private DeserializationContext context;
  private String agreementJson;
  private Validator validator;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    deserializer = new TradeAgreementDeserializer();
    mapper = new ObjectMapper();
    context = mapper.getDeserializationContext();
    agreementJson = getJsonFromFile("TradeAgreement/Correct_IBM_Agreement.json");
    ValidatorFactory factory = Validation.byDefaultProvider()
        .configure()
        .buildValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void testDeserialize() throws Exception {
    JsonParser jsonParser = mapper.getFactory().createParser(agreementJson);
    TradeAgreement agreement = deserializer.deserialize(jsonParser, context);

    assertEquals("IBM", agreement.getInstrument());
    assertEquals("INT-1", agreement.getInternalParty());
    assertEquals("EXT-1", agreement.getExternalParty());
    assertEquals("B", agreement.getBuySell());
    assertEquals(new Double(100.0), agreement.getQty());
    assertEquals(BigMoney.parse("GBP 100"), agreement.getProceeds());
  }

  @Test
  public void testDeserializeMissingFields() throws Exception {
    JsonParser jsonParser =
        mapper
            .getFactory()
            .createParser(getJsonFromFile("TradeAgreement/Missing_Fields_IBM_Agreement.json"));

    TradeAgreement agreement = deserializer.deserialize(jsonParser, context);
    List<ConstraintViolation<TradeAgreement>> constraintViolations = validator.validate(agreement)
        .stream().sorted(Comparator.comparing(a -> a.getPropertyPath().toString()))
        .collect(Collectors.toList());
    Iterator<ConstraintViolation<TradeAgreement>> iter = constraintViolations.iterator();
    ConstraintViolation<TradeAgreement> buySellViolation = iter.next();
    ConstraintViolation<TradeAgreement> externalPartyViolation = iter.next();

    assertEquals(2, constraintViolations.size());
    assertEquals("must not be null", buySellViolation.getMessage());
    assertEquals("buySell", buySellViolation.getPropertyPath().toString());
    assertEquals("must not be null", externalPartyViolation.getMessage());
    assertEquals("externalParty", externalPartyViolation.getPropertyPath().toString());
  }

  @Test
  public void testDeserializeIncorrectFields() throws Exception {
    JsonParser jsonParser =
        mapper
            .getFactory()
            .createParser(getJsonFromFile("TradeAgreement/Incorrect_Fields_IBM_Agreement.json"));

    TradeAgreement agreement = deserializer.deserialize(jsonParser, context);
    List<ConstraintViolation<TradeAgreement>> constraintViolations = validator.validate(agreement)
        .stream().sorted(Comparator.comparing(a -> a.getPropertyPath().toString()))
        .collect(Collectors.toList());
    Iterator<ConstraintViolation<TradeAgreement>> iter = constraintViolations.iterator();
    ConstraintViolation<TradeAgreement> proceedsViolation = iter.next();
    ConstraintViolation<TradeAgreement> qtyViolation = iter.next();

    assertEquals(2, constraintViolations.size());
    assertEquals("must not be null", qtyViolation.getMessage());
    assertEquals("qty", qtyViolation.getPropertyPath().toString());
    assertEquals("must not be null", proceedsViolation.getMessage());
    assertEquals("proceeds", proceedsViolation.getPropertyPath().toString());
  }

  @Test
  public void testDeserializeMissingIncorrectFields() throws Exception {
    JsonParser jsonParser =
        mapper
            .getFactory()
            .createParser(
                getJsonFromFile("TradeAgreement/Missing_Incorrect_Fields_IBM_Agreement.json"));

    TradeAgreement agreement = deserializer.deserialize(jsonParser, context);
    List<ConstraintViolation<TradeAgreement>> constraintViolations = validator.validate(agreement)
        .stream().sorted(Comparator.comparing(a -> a.getPropertyPath().toString()))
        .collect(Collectors.toList());
    Iterator<ConstraintViolation<TradeAgreement>> iter = constraintViolations.iterator();
    ConstraintViolation<TradeAgreement> buySellViolation = iter.next();
    ConstraintViolation<TradeAgreement> proceedsViolation = iter.next();

    assertEquals(2, constraintViolations.size());
    assertEquals("must not be null", proceedsViolation.getMessage());
    assertEquals("proceeds", proceedsViolation.getPropertyPath().toString());
    assertEquals("must not be null", buySellViolation.getMessage());
    assertEquals("buySell", buySellViolation.getPropertyPath().toString());
  }

  @Test
  public void testGetProceeds() throws Exception {
    JsonNode node = mapper.readTree(agreementJson);
    BigMoney proceeds = deserializer.getProceeds(node);

    assertEquals(BigMoney.parse("GBP 100"), proceeds);
  }

  @Test
  public void testInvalidProceedsReturnsNull() throws Exception {
    String incorrectAgreementJson =
        getJsonFromFile("TradeAgreement/Incorrect_Fields_IBM_Agreement.json");
    JsonNode node = mapper.readTree(incorrectAgreementJson);

    BigMoney proceeds = deserializer.getProceeds(node);
    assertEquals(null, proceeds);
  }
}
