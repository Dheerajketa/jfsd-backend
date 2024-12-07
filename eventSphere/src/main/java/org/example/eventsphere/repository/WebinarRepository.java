package org.example.eventsphere.repository;
import java.util.List;
import java.util.Optional;

import org.example.eventsphere.model.Webinar;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WebinarRepository extends MongoRepository<Webinar, String> {
    Optional<Webinar> findByWebinarId(String webinarId);
    List<Webinar> findByInstructorUsername(String instructorUsername);
}
