/**
 * The service interface for managing Student entities. It provides a method to retrieve a
 * collection of students associated with a specific project ID.
 */
package com.github.dreamteam.services;

import java.util.Collection;
import org.bson.Document;

public interface StudentService {
  public Collection<Document> getStudentsByProject(Long projectId);
}
