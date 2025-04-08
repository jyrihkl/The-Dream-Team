/** The main package for the Dreamteam application. */
package com.github.dreamteam;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

/**
 * The configuration class for MongoDB client settings. It extends the
 * AbstractMongoClientConfiguration class to provide custom MongoDB client settings.
 *
 * <p>This class is responsible for configuring the MongoDB client, including the connection string
 * and database name. It uses Spring's @Value annotation to inject the connection string and
 * database name from application properties.
 */
@Configuration
public class MongoClientConfig extends AbstractMongoClientConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoClientConfig.class);

  @Value("${spring.data.mongodb.database}")
  private String databaseName;

  @Value("${spring.data.mongodb.uri}")
  private String connectionString;

  /**
   * Returns the MongoDB database name.
   *
   * @return The name of the database.
   */
  @Override
  protected String getDatabaseName() {
    return databaseName;
  }

  /**
   * Returns the MongoDB client settings.
   *
   * <p>This method is responsible for configuring the MongoDB client settings, including the
   * connection string. It uses the {@link ConnectionString} class to parse the connection string
   * and create a {@link MongoClientSettings} object. The connection string is logged for debugging
   * purposes, but the credentials are redacted for security reasons.
   *
   * @return The MongoClientSettings object containing the connection settings.
   */
  @Bean
  public MongoClientSettings mongoClientSettings() {
    // Log connection string, but leave out credentials
    String redactedString = connectionString.replaceAll("://[^:]+:([^@]+)@", "://<credentials>@");
    LOGGER.info("Connecting to MongoDB at {}", redactedString);
    return MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(connectionString))
        .build();
  }
}
