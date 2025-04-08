/**
 * The service implementation for managing Project entities. It provides methods to retrieve all
 * projects with an optional limit on the number of projects returned.
 */
package com.github.dreamteam.services;

import com.github.dreamteam.exceptions.EntityNotFoundException;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * The service implementation for managing Project entities. It provides methods to retrieve all
 * projects with an optional limit on the number of projects returned.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);
  private final MongoCollection<Document> projectCollection;

  // TODO: Add index

  /**
   * Constructor for ProjectServiceImpl. Initializes the MongoDB collection for projects.
   *
   * @param mongoTemplate The MongoTemplate instance used to interact with MongoDB.
   */
  public ProjectServiceImpl(MongoTemplate mongoTemplate) {
    this.projectCollection = mongoTemplate.getCollection("projects");
  }

  /**
   * Retrieves all projects from the MongoDB database with an optional limit on the number of
   * projects returned.
   *
   * @param limit The maximum number of projects to retrieve. If 0, retrieves all projects.
   * @return A collection of projects.
   * @throws EntityNotFoundException if no projects are found.
   */
  public Collection<Document> getAllProjects(int limit) {
    LOGGER.info("Fetching all projects from MongoDB with limit {}", limit);
    // connect to MongoDB and fetch all projects
    List<Document> projects = projectCollection.find().limit(limit).into(new ArrayList<>());
    if (projects.isEmpty()) {
      throw new EntityNotFoundException("No projects found");
    }
    return projects;
  }
}
