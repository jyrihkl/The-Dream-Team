/**
 * This package has all the controllers for the backend. It is responsible for handling HTTP
 * requests and responses.
 */
package com.github.dreamteam.controllers;

import com.github.dreamteam.exceptions.EntityNotFoundException;
import com.github.dreamteam.services.ProjectServiceImpl;
import com.github.dreamteam.services.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Optional;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * ProjectController is responsible for handling HTTP requests related to projects. It provides
 * endpoints for retrieving project information, managing students, and interacting with the ML
 * component.
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

  @Autowired private ProjectServiceImpl projectService;

  @Autowired private StudentService studentService;

  /**
   * Retrieves a collection of projects based on the provided status and limit parameters.
   *
   * @param status Optional status filter for projects.
   * @param limit Optional limit on the number of projects to retrieve.
   * @return A collection of projects matching the criteria.
   */
  @GetMapping
  public Collection<Document> getProjects(
      @RequestParam(required = false) Optional<String> status,
      @RequestParam(required = false) Optional<Integer> limit) {
    LOGGER.info("Fetching all projects from MongoDB with status {}", status.orElse("all"));
    if (limit.isPresent()) {
      return projectService.getAllProjects(limit.get());
    } else {
      return projectService.getAllProjects(0);
    }
  }

  /**
   * Retrieves a collection of students associated with a specific project.
   *
   * @param projectId The ID of the project for which to retrieve students.
   * @return A collection of students associated with the specified project.
   */
  @GetMapping("/{projectId}/students")
  public Collection<Document> getStudents(@PathVariable Long projectId) {
    LOGGER.info("Fetching all students from MongoDB for project {}", projectId);
    return studentService.getStudentsByProject(projectId);
  }

  /**
   * Redirects to the ML component for making predictions.
   *
   * @param request The HTTP request object.
   * @return A ModelAndView object redirecting to the ML component's prediction page.
   */
  @PostMapping("/predict")
  public ModelAndView predict(HttpServletRequest request) {
    request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.PERMANENT_REDIRECT);
    LOGGER.info("Redirecting to ML component for prediction.");
    return new ModelAndView("redirect:/ml/score/predict");
  }

  /**
   * Retrieves a collection of scores associated with a specific project.
   *
   * @param request The HTTP request object.
   * @param projectId The ID of the project for which to retrieve scores.
   * @param scoreFile Optional parameter for specifying a score file.
   * @return A ModelAndView object redirecting to the scores page.
   */
  @GetMapping("/{projectId}/scores")
  public ModelAndView getScores(
      HttpServletRequest request,
      @PathVariable Long projectId,
      @RequestParam(required = false) Optional<String> scoreFile) {
    request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.PERMANENT_REDIRECT);
    LOGGER.info(
        "Redirecting to ML component for scores of project {} using score file {}",
        projectId,
        scoreFile.orElse("default"));
    StringBuilder redirectUrl =
        new StringBuilder("redirect:/ml/score/scores?projectId=").append(projectId);
    if (scoreFile.isPresent()) {
      redirectUrl.append("&scoreFile=").append(scoreFile.get());
    }
    return new ModelAndView(redirectUrl.toString());
  }

  /**
   * Retrieves a collection of scores associated with all projects.
   *
   * @param request The HTTP request object.
   * @param scoreFile Optional parameter for specifying a score file.
   * @return A ModelAndView object redirecting to the scores page.
   */
  @GetMapping("/scores")
  public ModelAndView getAllScores(
      HttpServletRequest request, @RequestParam(required = false) Optional<String> scoreFile) {
    request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.PERMANENT_REDIRECT);
    LOGGER.info(
        "Redirecting to ML component for all scores using score file {}",
        scoreFile.orElse("default"));
    StringBuilder redirectUrl = new StringBuilder("redirect:/ml/score/scores");
    if (scoreFile.isPresent()) {
      redirectUrl.append("?scoreFile=").append(scoreFile.get());
    }
    return new ModelAndView(redirectUrl.toString());
  }

  /**
   * Builds a team for a specific project using the ML component.
   *
   * @param request The HTTP request object.
   * @param projectId The ID of the project for which to build a team.
   * @param size Optional parameter for specifying the size of the team.
   * @param dataFile Optional parameter for specifying a data file.
   * @param saveFile Optional parameter for specifying a save file.
   * @return A ModelAndView object redirecting to the build team page.
   */
  @PostMapping("{projectId}/team")
  public ModelAndView buildTeam(
      HttpServletRequest request,
      @PathVariable Long projectId,
      @RequestParam(required = false) Optional<Integer> size,
      @RequestParam(required = false) Optional<String> dataFile,
      @RequestParam(required = false) Optional<String> saveFile) {
    request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.PERMANENT_REDIRECT);
    LOGGER.info(
        "Redirecting to ML component for building team for project {} with size {} using data file"
            + " {} and save file {}",
        projectId,
        size.orElse(0),
        dataFile.orElse("default"),
        saveFile.orElse("default"));
    StringBuilder redirectUrl =
        new StringBuilder("redirect:/ml/team/build-team?projectId=").append(projectId);
    if (size.isPresent()) {
      redirectUrl.append("&size=").append(size.get());
    }
    if (dataFile.isPresent()) {
      redirectUrl.append("&data=").append(dataFile.get());
    }
    if (saveFile.isPresent()) {
      redirectUrl.append("&saveFile=").append(saveFile.get());
    }
    return new ModelAndView(redirectUrl.toString());
  }

  /**
   * Handles EntityNotFoundException and returns a 404 Not Found response.
   *
   * @param e The EntityNotFoundException to handle.
   */
  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "MongoDB didn't find any document.")
  public final void handleNotFoundExceptions(EntityNotFoundException e) {
    LOGGER.info("=> Not found: {}", e.toString());
  }

  /**
   * Handles RuntimeException and returns a 500 Internal Server Error response.
   *
   * @param e The RuntimeException to handle.
   */
  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Server Error")
  public final void handleAllExceptions(RuntimeException e) {
    LOGGER.error("=> Internal server error.", e);
  }
}
