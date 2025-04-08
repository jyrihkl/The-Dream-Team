/**
 * The service interface for managing Student entities. It provides a method to retrieve a
 * collection of students associated with a specific project ID.
 */
package com.github.dreamteam.services;

import java.util.Collection;
import org.bson.Document;

/**
 * The service interface for managing Student entities. It provides a method to retrieve a
 * collection of students associated with a specific project ID.
 */
public interface StudentService {
  /**
   * Retrieves a collection of students associated with a specific project ID from the MongoDB
   * database.
   *
   * @param projectId The ID of the project for which to retrieve students.
   * @return A collection of students associated with the specified project.
   */
  public Collection<Document> getStudentsByProject(Long projectId);
}
