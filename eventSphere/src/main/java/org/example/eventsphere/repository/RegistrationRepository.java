package org.example.eventsphere.repository;

import java.util.List;
import java.util.Optional;

import org.example.eventsphere.model.Registration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegistrationRepository extends MongoRepository<Registration, String> {
    Optional<Registration> findByUsernameAndWebinarId(String username, String webinarId);
    Optional<Registration> findByRegistrationId(String registrationId);
    List<Registration> findByWebinarId(String webinarId);
    List<Registration> findByUsername(String username);
    List<Registration> findByUsernameAndStatus(String username, String status);
}
