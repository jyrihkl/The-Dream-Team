/**
 * The service interface for managing Project entities. It provides methods to retrieve all projects
 * with an optional limit on the number of projects returned.
 */
package com.github.dreamteam.services;

import java.util.Collection;
import org.bson.Document;

public interface ProjectService {
  public Collection<Document> getAllProjects(int limit);
}
