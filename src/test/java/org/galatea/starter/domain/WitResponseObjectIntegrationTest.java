package org.galatea.starter.domain;

import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import java.util.ArrayList;
import java.util.List;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.RestClientConfig;
import org.galatea.starter.domain.wit.EntityStore;
import org.junit.Test;
import java.lang.reflect.Field;
import org.junit.experimental.categories.Category;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;

import static org.junit.Assert.assertEquals;



//This allows us to autowire the beans defined in RestClientConfig.java
@SpringBootTest(classes = {RestClientConfig.class})
@Import({FeignAutoConfiguration.class})
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class WitResponseObjectIntegrationTest extends ASpringTest{

  /*
  Here we define an interface similar to that in the class WitGetter.java except here we
  are getting a list of all entities our app uses.

  TODO: Can we use a Spring Boot FeignClient similar to QuoteGetter.java or WitGetter.java here
  instead of the standard Feign interface / builder?
   */
  public interface TestWitGetter{
    @Headers("Authorization: Bearer MMGURXBKQ3YVKYMGDUJQ2K3CKBNMNEVS")
    @RequestLine("GET /entities?")
    String[] getWitResponse();
  }
  TestWitGetter testGetter = Feign.builder()
      .decoder(new JacksonDecoder()).target(TestWitGetter.class, "https://api.wit.ai");

  /*
  make sure our custom Entities defined in wit.ai match those specified in EntityStore.java.
  If this fails it means entities were added to the wit.ai app but not to Entity store
  and Fuse wont extract them from wit.ai
   */
  @Test
  public void allCustomFieldsMatch() {
    boolean failed = false;
    /*
    get an array of the fields declared in our EntityStore object.
    Then populate an ArrayList<String> with their field names.
     */
    Field[] fields = EntityStore.class.getDeclaredFields();
    List<String> fieldNames = new ArrayList<>();
    for (Field field : fields){
      fieldNames.add(field.getName());
    }
    /*
    Here we retrieve a list of the entities from our wit app. Our list of EntityStore field names
    should match up with this list. wit.ai will return its built in entity names along with our
    custom ones regardless of our usage so we ignore them in the loop.
     */
    String[] entities = testGetter.getWitResponse();

    for (String entity : entities){
      if (entity.contains("wit$")) continue;
      if (!fieldNames.contains(entity)){
        //test will fail immediately on the first missing entity.
        failed = true;
        break;
      }
    }
    assertEquals(false,failed);
  }

}
