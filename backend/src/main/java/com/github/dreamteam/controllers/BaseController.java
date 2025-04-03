package com.github.dreamteam.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/")
public class BaseController {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
  private final RestTemplate restTemplate = new RestTemplate();
  private final String ML_BASE_URL = "http://ml:9696";

  @RequestMapping("/")
  public String index() {
    return "Greetings from Spring Boot!";
  }

  @PostMapping("/init")
  public ResponseEntity<String> initialize() {
    LOGGER.info("Received request to initialize ML");
    try {
      ResponseEntity<String> cleanResponse = restTemplate.postForEntity(ML_BASE_URL + "/data/clean", null,
          String.class);
      if (cleanResponse.getStatusCode().is2xxSuccessful()) {
        restTemplate.postForEntity(ML_BASE_URL + "/training/train", null, String.class);
        return ResponseEntity.ok("Initialization complete");
      } else {
        return ResponseEntity.status(cleanResponse.getStatusCode()).body("Data cleaning failed");
      }
    } catch (Exception e) {
      LOGGER.error("Error during initialization", e);
      return ResponseEntity.status(500).body("Initialization failed");
    }
  }

  @PostMapping("/clean")
  public ResponseEntity<String> cleanData(@RequestParam(required = false) Optional<String> dataCleaner) {
    LOGGER.info("Received request to clean data with cleaner: {}", dataCleaner.orElse("default"));

    // Call the ML service to clean data
    try {
      String cleaner = dataCleaner.isPresent() ? "?cleaner=" + dataCleaner.get() : "";
      ResponseEntity<String> response = restTemplate.postForEntity(
          ML_BASE_URL + "/data/clean" + cleaner, null, String.class);
      return ResponseEntity.ok(response.getBody());
    } catch (Exception e) {
      LOGGER.error("Error during data cleaning", e);
      return ResponseEntity.status(500).body("Data cleaning failed");
    }

  }

  @PostMapping("/train")
  public ResponseEntity<String> trainModel(@RequestParam(required = false) Optional<String> modelName) {
    LOGGER.info("Received request to train model: {}", modelName.orElse("default"));

    // Call the ML service to train the model
    try {
      String model = modelName.isPresent() ? "?model=" + modelName.get() : "";
      ResponseEntity<String> response = restTemplate.postForEntity(
          ML_BASE_URL + "/training/train" + model, null, String.class);
      return ResponseEntity.ok(response.getBody());
    } catch (Exception e) {
      LOGGER.error("Error during model training", e);
      return ResponseEntity.status(500).body("Model training failed");
    }

  }

  @PostMapping("/train/all")
  public ResponseEntity<String> trainAllModels() {
    // Return message saying that this is not implemented yet
    LOGGER.info("Received request to train all models");
    return ResponseEntity.ok("Training all models is not implemented yet");

    // TODO: Implement when properly supported
    // return restTemplate.postForEntity(ML_BASE_URL + "/training/train", null,
    // String.class);
  }

  @PostMapping("/clean/all")
  public ResponseEntity<String> cleanAllData() {
    // Return message saying that this is not implemented yet
    LOGGER.info("Received request to clean all data");
    return ResponseEntity.ok("Cleaning all data is not implemented yet");

    // TODO: Implement when properly supported
    // return restTemplate.postForEntity(ML_BASE_URL + "/data/clean", null,
    // String.class);
  }
}
