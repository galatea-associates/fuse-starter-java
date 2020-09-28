package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

}
