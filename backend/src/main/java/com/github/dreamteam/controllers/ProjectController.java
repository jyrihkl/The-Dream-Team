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
import org.springframework.web.util.UriComponentsBuilder;

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
   * Redirects to the ML component for making predictions based on the provided parameters.
   *
   * @param request The HTTP request object.
   * @param modelType Optional parameter for specifying the model type.
   * @param modelName Optional parameter for specifying the model name.
   * @param data Optional parameter for specifying the data to be used for predictions.
   * @param cleaning Optional parameter for specifying whether to clean the data.
   * @param saveFile Optional parameter for specifying a save file.
   * @return A ModelAndView object redirecting to the ML component for predictions.
   */
  @PostMapping("/predict")
  public ModelAndView predict(
      HttpServletRequest request,
      @RequestParam(required = false) Optional<String> modelType,
      @RequestParam(required = false) Optional<String> modelName,
      @RequestParam(required = false) Optional<String> data,
      @RequestParam(required = false) Optional<Boolean> cleaning,
      @RequestParam(required = false) Optional<String> saveFile) {
    request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.PERMANENT_REDIRECT);
    LOGGER.info(
        "Redirecting to ML component for predictions with params: modelType {}, modelName {}, data"
            + " {}, cleaning {}, saveFile {}",
        modelType.orElse("default"),
        modelName.orElse("default"),
        data.orElse("default"),
        cleaning.orElse(false),
        saveFile.orElse("default"));

    UriComponentsBuilder builder = UriComponentsBuilder.fromPath("redirect:/ml/score/predict");
    modelType.ifPresent(type -> builder.queryParam("modelType", type));
    modelName.ifPresent(name -> builder.queryParam("modelName", name));
    data.ifPresent(d -> builder.queryParam("data", d));
    cleaning.ifPresent(c -> builder.queryParam("cleaning", c));
    saveFile.ifPresent(file -> builder.queryParam("saveFile", file));
    String redirectUrl = builder.toUriString();

    return new ModelAndView(redirectUrl);
  }

  /**
   * Retrieves a collection of scores associated with a specific project.
   *
   * @param request The HTTP request object.
   * @param projectId The ID of the project for which to retrieve scores.
   * @param applicantsFile Optional parameter for specifying an applicants file.
   * @param scoreFile Optional parameter for specifying a score file.
   * @param motivationFile Optional parameter for specifying a motivation file.
   * @return A ModelAndView object redirecting to the scores page.
   */
  @GetMapping("/{projectId}/scores")
  public ModelAndView getScores(
      HttpServletRequest request,
      @PathVariable Long projectId,
      @RequestParam(required = false) Optional<String> applicantsFile,
      @RequestParam(required = false) Optional<String> scoreFile,
      @RequestParam(required = false) Optional<String> motivationFile) {
    request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.PERMANENT_REDIRECT);
    LOGGER.info(
        "Redirecting to ML component for scores with projectId {}, applicantsFile {}, scoreFile {},"
            + " motivationFile {}",
        projectId,
        applicantsFile.orElse("default"),
        scoreFile.orElse("default"),
        motivationFile.orElse("default"));

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromPath("redirect:/ml/score/scores")
            .queryParam("projectId", projectId);
    applicantsFile.ifPresent(file -> builder.queryParam("applicantsFile", file));
    scoreFile.ifPresent(file -> builder.queryParam("scoreFile", file));
    motivationFile.ifPresent(file -> builder.queryParam("motivationFile", file));

    return new ModelAndView(builder.toUriString());
  }

  /**
   * Redirects to the ML component for retrieving all scores.
   *
   * @param request The HTTP request object.
   * @param applicantsFile Optional parameter for specifying an applicants file.
   * @param scoreFile Optional parameter for specifying a score file.
   * @param motivationFile Optional parameter for specifying a motivation file.
   * @return A ModelAndView object redirecting to the scores page.
   */
  @GetMapping("/scores")
  public ModelAndView getAllScores(
      HttpServletRequest request,
      @RequestParam(required = false) Optional<String> applicantsFile,
      @RequestParam(required = false) Optional<String> scoreFile,
      @RequestParam(required = false) Optional<String> motivationFile) {
    request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.PERMANENT_REDIRECT);
    LOGGER.info(
        "Redirecting to ML component for all scores with applicantsFile {}, scoreFile {}, "
            + "motivationFile {}",
        applicantsFile.orElse("default"),
        scoreFile.orElse("default"),
        motivationFile.orElse("default"));

    UriComponentsBuilder builder = UriComponentsBuilder.fromPath("redirect:/ml/score/scores");
    applicantsFile.ifPresent(file -> builder.queryParam("applicantsFile", file));
    scoreFile.ifPresent(file -> builder.queryParam("scoreFile", file));
    motivationFile.ifPresent(file -> builder.queryParam("motivationFile", file));

    return new ModelAndView(builder.toUriString());
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

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromPath("redirect:/ml/team/build-team")
            .queryParam("projectId", projectId);
    size.ifPresent(s -> builder.queryParam("size", s));
    dataFile.ifPresent(file -> builder.queryParam("data", file));
    saveFile.ifPresent(file -> builder.queryParam("saveFile", file));

    return new ModelAndView(builder.toString());
  }

  /**
   * Builds a dream team for a specific project using the ML component.
   *
   * @param request The HTTP request object.
   * @param projectId The ID of the project for which to build a dream team.
   * @param size Optional parameter for specifying the size of the dream team.
   * @param applicants Optional parameter for specifying the applicants.
   * @param scores Optional parameter for specifying the scores.
   * @param motivations Optional parameter for specifying the motivations.
   * @param saveFile Optional parameter for specifying a save file.
   * @return A ModelAndView object redirecting to the build dream team page.
   */
  @PostMapping("/{projectId}/dream-team")
  public ModelAndView buildDreamTeam(
      HttpServletRequest request,
      @PathVariable Long projectId,
      @RequestParam(required = false) Optional<Integer> size,
      @RequestParam(required = false) Optional<String> applicants,
      @RequestParam(required = false) Optional<String> scores,
      @RequestParam(required = false) Optional<String> motivations,
      @RequestParam(required = false) Optional<String> saveFile) {
    request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.PERMANENT_REDIRECT);
    LOGGER.info(
        "Redirecting to ML component for building dream team for project {} with size {} using"
            + " applicants {}, scores {}, motivations {}, and save file {}",
        projectId,
        size.orElse(0),
        applicants.orElse("default"),
        scores.orElse("default"),
        motivations.orElse("default"),
        saveFile.orElse("default"));

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromPath("redirect:/ml/team/dream-team")
            .queryParam("projectId", projectId);
    size.ifPresent(s -> builder.queryParam("team_size", s));
    applicants.ifPresent(a -> builder.queryParam("applicants", a));
    scores.ifPresent(s -> builder.queryParam("scores", s));
    motivations.ifPresent(m -> builder.queryParam("motivations", m));
    saveFile.ifPresent(file -> builder.queryParam("save_file", file));

    return new ModelAndView(builder.toUriString());
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
