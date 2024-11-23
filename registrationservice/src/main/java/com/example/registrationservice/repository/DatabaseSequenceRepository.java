package com.example.registrationservice.repository;

import com.example.registrationservice.model.DatabaseSequence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DatabaseSequenceRepository extends MongoRepository<DatabaseSequence, String> {
}
