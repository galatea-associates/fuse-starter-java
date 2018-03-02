package org.galatea.starter.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.utils.DelimitedJsonFileParser;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class DelimitedJsonFileParserTest {

  @ClassRule
  public static TemporaryFolder folder = new TemporaryFolder();

  private DelimitedJsonFileParser parser;

  private static final String DELIMITER = "\\|";
  private static final String VALID_JSON = "{ \"name\" : \"Sam\", \"age\" : 10}";

  @Before
  public void setUp() {
    parser = new DelimitedJsonFileParser(DELIMITER, new ObjectMapper());
  }

  @Test
  public void parsesValidJson() throws Exception {
    File file = writeStringToNewFile(VALID_JSON);

    List<TestObject> parsedObjects = parser.parseFile(file, TestObject.class);
    assertEquals("Sam", parsedObjects.get(0).name);
    assertEquals(10, parsedObjects.get(0).age);
  }

  @Test
  public void handlesInvalidInputWithoutException() throws Exception {
    String json = "invalid json" + DELIMITER + VALID_JSON;
    File file = writeStringToNewFile(json);

    List<TestObject> parsedObjects = parser.parseFile(file, TestObject.class);
    assertEquals(1, parsedObjects.size());
  }

  private File writeStringToNewFile(String content) throws Exception {
    File file = folder.newFile();
    Files.write(Paths.get(file.getAbsolutePath()), content.getBytes());
    return file;
  }

  private static class TestObject {

    public String name;
    public int age;

  }

}