package org.galatea.starter.utils.deserializers;

import static org.galatea.starter.TestUtilities.getJsonFromFile;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import lombok.extern.slf4j.Slf4j;

import org.galatea.starter.domain.FxRateResponse;
import org.galatea.starter.domain.TradeAgreement;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Slf4j
public class FxRateResponseDeserializerTest  {

  private FxRateResponseDeserializer deserializer;
  private ObjectMapper mapper;
  private DeserializationContext context;
  private String responseJson;
  private Validator validator;

  @Before
  public void setUp() throws Exception {
    deserializer = new FxRateResponseDeserializer();
    mapper = new ObjectMapper();
    context = mapper.getDeserializationContext();
    responseJson = getJsonFromFile("FxRateResponse/Correct_FX_Response.json");
    ValidatorFactory factory = Validation.byDefaultProvider()
        .configure()
        .buildValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void testDeserialize() throws Exception {
    JsonParser jsonParser = mapper.getFactory().createParser(responseJson);
    FxRateResponse response = deserializer.deserialize(jsonParser, context);

    assertEquals(BigDecimal.valueOf(1.3467), response.getExchangeRate());
    assertEquals(CurrencyUnit.GBP, response.getBaseCurrency());
    assertEquals(LocalDate.parse("2017-11-30"), response.getValidOn());
  }

  @Test
  public void testDeserializeIncorrectFields() throws Exception {
    JsonParser jsonParser =
        mapper
            .getFactory()
            .createParser(getJsonFromFile("FxRateResponse/Incorrect_Fields_FX_Response.json"));

    FxRateResponse response = deserializer.deserialize(jsonParser, context);
    List<ConstraintViolation<FxRateResponse>> constraintViolations = validator.validate(response)
        .stream().sorted(Comparator.comparing(a -> a.getPropertyPath().toString()))
        .collect(Collectors.toList());
    Iterator<ConstraintViolation<FxRateResponse>> iter = constraintViolations.iterator();
    ConstraintViolation<FxRateResponse> exchangeRateViolation = iter.next();
    ConstraintViolation<FxRateResponse> validOnViolation = iter.next();

    assertEquals(2, constraintViolations.size());
    assertEquals("must not be null", exchangeRateViolation.getMessage());
    assertEquals("exchangeRate", exchangeRateViolation.getPropertyPath().toString());
    assertEquals("must not be null", validOnViolation.getMessage());
    assertEquals("validOn", validOnViolation.getPropertyPath().toString());
  }

  @Test
  public void testDeserializeMissingField() throws Exception {
    JsonParser jsonParser =
        mapper
            .getFactory()
            .createParser(getJsonFromFile("FxRateResponse/Missing_Field_FX_Response.json"));

    FxRateResponse response = deserializer.deserialize(jsonParser, context);
    Set<ConstraintViolation<FxRateResponse>> constraintViolations = validator.validate(response);
    ConstraintViolation<FxRateResponse> validOnViolation = constraintViolations.iterator().next();

    assertEquals(1, constraintViolations.size());
    assertEquals("must not be null", validOnViolation.getMessage());
    assertEquals("validOn", validOnViolation.getPropertyPath().toString());
  }

  @Test
  public void testDeserializeMissingIncorrectFields() throws Exception {
    JsonParser jsonParser =
        mapper
            .getFactory()
            .createParser(
                getJsonFromFile("FxRateResponse/Missing_Incorrect_Fields_FX_Response.json"));

    FxRateResponse response = deserializer.deserialize(jsonParser, context);
    List<ConstraintViolation<FxRateResponse>> constraintViolations = validator.validate(response)
        .stream().sorted(Comparator.comparing(a -> a.getPropertyPath().toString()))
        .collect(Collectors.toList());
    Iterator<ConstraintViolation<FxRateResponse>> iter = constraintViolations.iterator();
    ConstraintViolation<FxRateResponse> baseCurrencyViolation = iter.next();
    ConstraintViolation<FxRateResponse> validOnViolation = iter.next();

    assertEquals(2, constraintViolations.size());
    assertEquals("must not be null", baseCurrencyViolation.getMessage());
    assertEquals("baseCurrency", baseCurrencyViolation.getPropertyPath().toString());
    assertEquals("must not be null", validOnViolation.getMessage());
    assertEquals("validOn", validOnViolation.getPropertyPath().toString());
  }

  @Test
  public void testGetDate() throws Exception {
    JsonNode node = mapper.readTree(responseJson);
    LocalDate validOn = deserializer.getDate(node);

    assertEquals(LocalDate.parse("2017-11-30"), validOn);
  }

  @Test
  public void testInvalidGetDateReturnsNull() throws Exception {
    String incorrectResponseJson =
        getJsonFromFile("FxRateResponse/Incorrect_Fields_FX_Response.json");
    JsonNode node = mapper.readTree(incorrectResponseJson);
    LocalDate validOn = deserializer.getDate(node);
    assertEquals(null, validOn);
  }
}
