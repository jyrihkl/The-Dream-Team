/** The main package for the Dreamteam application. */
package com.github.dreamteam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * Abstract base test class for setting up a MongoDB container for integration testing.
 *
 * <p>This class initializes a MongoDB container using Testcontainers and imports mock data from
 * JSON files into the database. It also provides dynamic properties for the Spring application
 * context to connect to the MongoDB instance.
 */
public class AbstractContainerBaseTest {

  protected static final MongoDBContainer mongoDBContainer;

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractContainerBaseTest.class);

  static {
    mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0.16"));
    mongoDBContainer.start();
    mongoDBContainer.copyFileToContainer(
        MountableFile.forClasspathResource("/mock_students.json"), "/mock_students.json");
    mongoDBContainer.copyFileToContainer(
        MountableFile.forClasspathResource("/mock_projects.json"), "/mock_projects.json");
    init();
  }

  /**
   * Initializes the MongoDB container by importing mock data from JSON files.
   *
   * <p>This method is called after the container is started and copies the mock data files into the
   * container. It then uses the `mongoimport` command to import the data into the specified
   * collections in the MongoDB database.
   */
  protected static final void init() {
    try {
      mongoDBContainer.execInContainer(
          "mongoimport",
          "-d",
          "testdb",
          "-c",
          "students",
          "--file",
          "/mock_students.json",
          "--jsonArray");
      mongoDBContainer.execInContainer(
          "mongoimport",
          "-d",
          "testdb",
          "-c",
          "projects",
          "--file",
          "/mock_projects.json",
          "--jsonArray");
    } catch (Exception e) {
      LOGGER.error("Error while importing mock data into MongoDB: ", e);
    }
  }

  /**
   * Sets up dynamic properties for the Spring application context to connect to the MongoDB
   * container.
   *
   * <p>This method is annotated with `@DynamicPropertySource`, which allows it to register dynamic
   * properties that will be used by the Spring application context during testing. It sets the
   * MongoDB URI and database name based on the container's configuration.
   *
   * @param registry The registry to add dynamic properties to.
   */
  @DynamicPropertySource
  static void mongoDbProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    registry.add("spring.data.mongodb.database", () -> "testdb");
  }
}
