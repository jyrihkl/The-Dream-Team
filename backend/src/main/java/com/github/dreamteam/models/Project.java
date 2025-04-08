/**
 * This package has all the models for the backend. It is responsible for defining the data
 * structures used in the application.
 */
package com.github.dreamteam.models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Project represents the data structure used for storing project-related information. It includes
 * fields such as project ID, project name, description, list of batch IDs, tags, and themes.
 */
@Document(collection = "projects")
@Data
@AllArgsConstructor
public class Project {
  @Id private String id;
  private String name;
  private String description;
  private List<Long> batchesIds;
  private List<String> tags;
  private List<String> themes;
}
