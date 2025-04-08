/**
 * The repository interface for managing Student entities in the MongoDB database. It extends the
 * MongoRepository interface, which provides basic CRUD operations.
 */
package com.github.dreamteam.repositories;

import com.github.dreamteam.models.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * The repository interface for managing Student entities in the MongoDB database. It extends the
 * MongoRepository interface, which provides basic CRUD operations.
 */
@Repository
public interface StudentRepository extends MongoRepository<Student, String> {}
