package org.example.eventsphere.repository;

import org.example.eventsphere.model.DatabaseSequence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DatabaseSequenceRepository extends MongoRepository<DatabaseSequence, String> {
}
