/**
 * This package has all the controllers for the backend. It is responsible for handling HTTP
 * requests and responses.
 */
package com.github.dreamteam.controllers;

import com.github.dreamteam.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * StudentController is responsible for handling HTTP requests related to students. It provides
 * endpoints for managing student information and interactions with the ML component.
 */
@RestController
@RequestMapping("/students")
public class StudentController {

  @Autowired private StudentService service;

  // TODO: Delete?
}
