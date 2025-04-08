/**
 * This package has all the models for the backend. It is responsible for defining the data
 * structures used in the application.
 */
package com.github.dreamteam.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ApplicationData represents the data structure used for storing application-related information.
 * It includes fields such as project ID, project name, student ID, chosen batch, relation, and
 * various explanations related to the application process.
 */
@Data
@AllArgsConstructor
public class ApplicationData {
  private String projectId;
  private String projectName;
  private String studentId;
  private Long chosenBatch;
  private String relation;
  private boolean staffInsertion;
  private String dropoutExplanation;
  private String whyExperience;
  private String whyProject;
}
