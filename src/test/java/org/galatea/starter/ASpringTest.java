
package org.galatea.starter;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public abstract class ASpringTest {

  public static String readData(final String fileName) throws IOException {
    return IOUtils.toString(ASpringTest.class.getClassLoader().getResourceAsStream(fileName))
        .trim();
  }

}
