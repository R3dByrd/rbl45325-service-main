// src/main/java/de/hfu/cnc/UserRepository.java
package de.hfu.cnc;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByLastName(String lastName);
}
