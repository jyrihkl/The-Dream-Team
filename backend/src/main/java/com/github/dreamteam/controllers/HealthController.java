/**
 * This package has all the controllers for the backend. It is responsible for handling HTTP
 * requests and responses.
 */
package com.github.dreamteam.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HealthController is responsible for providing health check endpoints for the service. It allows
 * users to verify if the service is running and accessible.
 */
@RestController
@RequestMapping("/health")
public class HealthController {
  @GetMapping
  public ResponseEntity<String> checkHealth() {
    return ResponseEntity.ok("Service is up and running");
  }
}
