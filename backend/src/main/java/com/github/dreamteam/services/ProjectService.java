/**
 * The service interface for managing Project entities. It provides methods to retrieve all projects
 * with an optional limit on the number of projects returned.
 */
package com.github.dreamteam.services;

import java.util.Collection;
import org.bson.Document;

/**
 * The service interface for managing Project entities. It provides methods to retrieve all projects
 * with an optional limit on the number of projects returned.
 */
public interface ProjectService {
  /**
   * Retrieves all projects from the MongoDB database with an optional limit on the number of
   * projects returned.
   *
   * @param limit The maximum number of projects to retrieve. If 0, retrieves all projects.
   * @return A collection of projects.
   */
  public Collection<Document> getAllProjects(int limit);
}
