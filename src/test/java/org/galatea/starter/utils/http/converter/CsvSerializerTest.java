package org.galatea.starter.utils.http.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Many of these tests serve to document certain behaviors of the Jackson CSV serialization library
 * used in the current implementation of the CsvSerializer class. Many of these tests may need to
 * change if a different implementation is used.
 */
@Slf4j
public class CsvSerializerTest {

  @Test
  public void serializeToCsv_basic() throws Exception {
    BasicClass row1 = new BasicClass("foo", "bar");
    BasicClass row2 = new BasicClass("baz", null);

    String expected
        = "field1,field2\n"
        + "foo,bar\n"
        + "baz,\n";
    String actual = CsvSerializer.serializeToCsv(Arrays.asList(row1, row2), BasicClass.class);
    assertEquals(expected, actual);
  }

  @Test
  public void serializeToCsv_collections() throws Exception {
    ClassWithCollections row1 = new ClassWithCollections(
        new String[] {"foo", "bar"}, Arrays.asList("baz", "qux"));

    // Note that the array and list are to-string'ed as item1;item2
    String expected
        = "field1,field2\n"
        + "foo;bar,baz;qux\n";
    String actual = CsvSerializer.serializeToCsv(
        Collections.singletonList(row1), ClassWithCollections.class);
    assertEquals(expected, actual);
  }

  @Test
  public void serializeToCsv_childClass() throws Exception {
    ChildClass row1 = new ChildClass("foo", "bar", "baz");

    String expected
        = "field1,field2,field3\n"
        + "foo,bar,baz\n";
    String actual = CsvSerializer.serializeToCsv(Collections.singletonList(row1), ChildClass.class);
    assertEquals(expected, actual);
  }

  @Test
  public void serializeToCsv_childClass2() throws Exception {
    BasicClass row1 = new ChildClass("foo", "bar", "baz");

    try {
      CsvSerializer.serializeToCsv(Collections.singletonList(row1), BasicClass.class);
      fail("A JsonProcessingException was expected but not thrown");
    } catch (JsonProcessingException e) {
      // Jackson CSV will create a schema with the properties of BasicClass, but then when it
      // attempts to serialize a ChildClass it will complain about the additional field that isn't
      // part of the original schema
      log.info("Caught expected JsonProcessingException with message: {}", e.getMessage());
    }
  }

  @Test
  public void serializeToCsv_composedObject() throws Exception {
    ClassHoldingOtherClass row1 = new ClassHoldingOtherClass(1, new BasicClass("foo", "bar"));

    try {
      CsvSerializer.serializeToCsv(Collections.singletonList(row1), ClassHoldingOtherClass.class);
      fail("A JsonProcessingException was expected but not thrown");
    } catch (JsonProcessingException e) {
      // Jackson CSV can't handle objects that hold other complex objects
      log.info("Caught expected JsonProcessingException with message: {}", e.getMessage());
    }
  }

  @Test
  public void serializeToCsv_missingGetters() throws Exception {
    ClassWithoutSomeGetters row1 = new ClassWithoutSomeGetters("foo", "bar");

    // Note that field2 is ignored, due to not having an associated getter method
    String expected
        = "field1\n"
        + "foo\n";
    String actual = CsvSerializer.serializeToCsv(
        Collections.singletonList(row1), ClassWithoutSomeGetters.class);
    assertEquals(expected, actual);
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  private static class BasicClass {

    private String field1;
    private String field2;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  private static class ClassWithCollections {

    private String[] field1;
    private List<String> field2;
  }

  @Getter
  private static class ChildClass extends BasicClass {

    private String field3;

    ChildClass(final String field1, final String field2, final String field3) {
      super(field1, field2);
      this.field3 = field3;
    }
  }

  @AllArgsConstructor
  @Getter
  private static class ClassHoldingOtherClass {

    private int field1;
    private BasicClass field2;
  }

  @AllArgsConstructor
  private static class ClassWithoutSomeGetters {

    @Getter
    private String field1;
    // No Getter
    private String field2;
  }
}