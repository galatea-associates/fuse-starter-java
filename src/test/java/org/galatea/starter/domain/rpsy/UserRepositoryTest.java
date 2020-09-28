package org.galatea.starter.domain.rpsy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class UserRepositoryTest extends ASpringTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  public void testMongo() throws Exception {
    User user = new User();
    user.setFirstName("test");
    user.setId("123");

    userRepository.save(user);
    assertEquals(1, userRepository.findAll().size());
    assertTrue(userRepository.findById("123").isPresent());
    assertEquals("test", userRepository.findById("123").get().getFirstName());
  }

}