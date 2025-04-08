/**
 * This package has all the controllers for the backend. It is responsible for handling HTTP
 * requests and responses.
 */
package com.github.dreamteam.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);

  /**
   * Health check endpoint to verify if the service is running.
   *
   * @return a response indicating the service status.
   */
  @GetMapping
  public ResponseEntity<String> checkHealth(HttpServletRequest request) {
    LOGGER.info("Health check endpoint called from {}", request.getRemoteAddr());
    return ResponseEntity.ok("Service is up and running");
  }
}
