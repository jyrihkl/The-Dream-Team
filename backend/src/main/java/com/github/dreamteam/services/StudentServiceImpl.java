/**
 * The service implementation for managing Student entities. It provides a method to retrieve a
 * collection of students associated with a specific project ID. The service uses MongoDB to store
 * and retrieve student data.
 */
package com.github.dreamteam.services;

import com.github.dreamteam.exceptions.EntityNotFoundException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * * The service implementation for managing Student entities. It provides a method to retrieve a
 * collection of students associated with a specific project ID. The service uses MongoDB to store
 * and retrieve student data.
 */
@Service
public class StudentServiceImpl implements StudentService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);
  private final MongoCollection<Document> studentCollection;

  /**
   * Constructs a new StudentServiceImpl instance with the provided MongoTemplate.
   *
   * @param mongoTemplate The MongoTemplate used to interact with the MongoDB database.
   */
  public StudentServiceImpl(MongoTemplate mongoTemplate) {
    this.studentCollection = mongoTemplate.getCollection("students");
  }

  /**
   * Retrieves a collection of students associated with a specific project ID from the MongoDB
   * database.
   *
   * @param projectId The ID of the project for which to retrieve students.
   * @return A collection of students associated with the specified project.
   * @throws EntityNotFoundException if no students are found for the specified project ID.
   */
  public Collection<Document> getStudentsByProject(Long projectId) {
    LOGGER.info("Fetching all students from MongoDB for project {}", projectId);
    Bson filter = Filters.eq("applications.projectId", projectId);
    List<Document> students = studentCollection.find(filter).into(new ArrayList<>());
    if (students.isEmpty()) {
      throw new EntityNotFoundException("No students found for project " + projectId);
    }
    return students;
  }
}
