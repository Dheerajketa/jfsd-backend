package org.example.webinarservice.repository;

import org.example.webinarservice.model.Webinar;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface WebinarRepository extends MongoRepository<Webinar, String> {
    Optional<Webinar> findByWebinarId(String webinarId);
    List<Webinar> findByInstructorUsername(String instructorUsername);
}
