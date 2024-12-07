package org.example.eventsphere.repository;
import java.util.Optional;

import org.example.eventsphere.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}