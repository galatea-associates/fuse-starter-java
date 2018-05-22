package org.galatea.starter.domain;

import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.util.ArrayList;
import java.util.List;
import junitparams.JUnitParamsRunner;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.RestClientConfig;
import org.galatea.starter.domain.wit.EntityStore;
import org.junit.Test;
import java.lang.reflect.Field;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.assertEquals;



//Should we extend ASpringTest?
@RunWith(JUnitParamsRunner.class)
@ContextConfiguration(classes = {RestClientConfig.class},initializers = ConfigFileApplicationContextInitializer.class)
@TestPropertySource(locations = {"classpath:application.yml"})
@ImportAutoConfiguration({RibbonAutoConfiguration.class, FeignRibbonClientAutoConfiguration.class, FeignAutoConfiguration.class})
@Category(org.galatea.starter.IntegrationTestCategory.class)
public class WitResponseObjectIntegrationTest extends ASpringTest{

  /*
  Here we define an interface similar to that in the class WitGetter.java except here we
  are getting a list of all entities our app uses.
   */
  public interface TestWitGetter{
    @Headers("Authorization: Bearer MMGURXBKQ3YVKYMGDUJQ2K3CKBNMNEVS")
    @RequestLine("GET /entities?")
    String[] getWitResponse();
  }
  //Any reason we should use @before like in other tests?
  TestWitGetter testGetter = Feign.builder()
      .decoder(new JacksonDecoder()).target(TestWitGetter.class, "https://api.wit.ai");

  /*
  make sure our custom Entities defined in wit.ai match those specified in EntityStore.java.
  If this fails it means entities were added to the wit.ai app but not to Entity store
  and Fuse wont extract them from wit.ai
   */
  @Test
  public void allCustomFieldsMatch() throws Exception {
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
