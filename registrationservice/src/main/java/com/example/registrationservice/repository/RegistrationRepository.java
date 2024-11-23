package com.example.registrationservice.repository;

import com.example.registrationservice.model.Registration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends MongoRepository<Registration, String> {
    Optional<Registration> findByRegistrationId(String registrationId);
    Optional<Registration> findByUsernameAndWebinarId(String username, String webinarId);
    List<Registration> findByWebinarId(String webinarId);
}
