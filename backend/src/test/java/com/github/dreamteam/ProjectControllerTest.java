/** The main package for the Dreamteam application. */
package com.github.dreamteam;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.dreamteam.controllers.ProjectController;
import com.github.dreamteam.services.ProjectServiceImpl;
import com.github.dreamteam.services.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.View;

/**
 * This class is responsible for testing the ProjectController class. It uses Mockito to mock the
 * dependencies and verify the behavior of the controller methods.
 *
 * <p>It includes tests for checking the retrieval of projects, students, and redirection to ML
 * components.
 */
@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

  @Mock private ProjectServiceImpl projectService;

  @Mock private StudentService studentService;

  @Mock private HttpServletRequest request;

  @InjectMocks private ProjectController projectController;

  /**
   * Initial setup for the test class. This method is executed before each test in the class. It
   * resets the mock objects to ensure a clean state for each test.
   */
  @BeforeEach
  void setUp() {
    reset(projectService, studentService, request);
  }

  /**
   * Tests the retrieval of projects without any limit. It verifies that the controller returns the
   * correct number of projects and their names.
   */
  @Test
  void testGetProjectsWithoutLimit() {
    when(projectService.getAllProjects(0)).thenReturn(List.of(new Document("name", "Project A")));

    var projects = projectController.getProjects(Optional.empty(), Optional.empty());

    assertThat(projects).hasSize(1);
    assertThat(projects.iterator().next().get("name")).isEqualTo("Project A");
  }

  /**
   * Tests the retrieval of projects with a limit. It verifies that the controller returns the
   * correct number of projects and their names.
   */
  @Test
  void testGetProjectsWithLimit() {
    when(projectService.getAllProjects(2))
        .thenReturn(List.of(new Document("name", "Project A"), new Document("name", "Project B")));

    var projects = projectController.getProjects(Optional.empty(), Optional.of(2));

    assertThat(projects).hasSize(2);
  }

  /**
   * Tests the retrieval of students associated with a specific project. It verifies that the
   * controller returns the correct number of students and their names.
   */
  @Test
  void testGetStudentsByProject() {
    when(studentService.getStudentsByProject(101L))
        .thenReturn(List.of(new Document("name", "John Doe")));

    var students = projectController.getStudents(101L);

    assertThat(students).hasSize(1);
    assertThat(students.iterator().next().get("name")).isEqualTo("John Doe");
  }

  /**
   * Tests the prediction redirection. It verifies that the controller redirects to the correct URL
   * and sets the response status to PERMANENT_REDIRECT.
   */
  @Test
  void testPredictRedirect() {
    var response = projectController.predict(request);

    assertThat(response.getViewName()).isEqualTo("redirect:/ml/score/predict");
    verify(request).setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.PERMANENT_REDIRECT);
  }

  /**
   * Tests the retrieval of scores associated with a specific project. It verifies that the
   * controller redirects to the correct URL and sets the response status to PERMANENT_REDIRECT.
   */
  @Test
  void testGetScoresRedirect() {
    var response = projectController.getScores(request, 101L, Optional.of("file.csv"));

    assertThat(response.getViewName())
        .isEqualTo("redirect:/ml/score/scores?projectId=101&scoreFile=file.csv");
    verify(request).setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.PERMANENT_REDIRECT);
  }
}
