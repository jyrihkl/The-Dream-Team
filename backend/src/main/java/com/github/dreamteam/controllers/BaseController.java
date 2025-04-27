/**
 * This package has all the controllers for the backend. It is responsible for handling HTTP
 * requests and responses.
 */
package com.github.dreamteam.controllers;

import java.util.Optional;
import org.bson.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * BaseController serves as a gateway for interacting with the ML component. It provides endpoints
 * for initializing, cleaning, and training ML models.
 */
@RestController
@RequestMapping("/")
public class BaseController {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
  private final RestTemplate restTemplate = new RestTemplate();
  private final String mlBaseUrl = "http://ml:9696";

  /**
   * Root endpoint to verify service availability.
   *
   * @return a greeting message.
   */
  @RequestMapping("/")
  public String index() {
    LOGGER.info("Root endpoint called");
    return "Greetings from Spring Boot!";
  }

  /**
   * Initializes the ML system by first cleaning data and then training a model and making a
   * prediction. This is a sequential process where each step depends on the success of the previous
   * one.
   *
   * @return ResponseEntity indicating success or failure.
   */
  @PostMapping("/init")
  public ResponseEntity<String> initialize() {
    LOGGER.info("Received request to initialize ML");
    try {
      ResponseEntity<String> cleanResponse =
          restTemplate.postForEntity(mlBaseUrl + "/data/clean", null, String.class);

      if (!cleanResponse.getStatusCode().is2xxSuccessful()) {
        LOGGER.error(
            "Data cleaning failed with status: {} and message: {}",
            cleanResponse.getStatusCode(),
            cleanResponse.getBody());
        return ResponseEntity.status(cleanResponse.getStatusCode())
            .body("Data cleaning failed with message: " + cleanResponse.getBody());
      }
      ResponseEntity<String> trainResponse =
          restTemplate.postForEntity(mlBaseUrl + "/training/train", null, String.class);

      if (!trainResponse.getStatusCode().is2xxSuccessful()) {
        LOGGER.error(
            "Model training failed with status: {} and message: {}",
            trainResponse.getStatusCode(),
            trainResponse.getBody());
        return ResponseEntity.status(trainResponse.getStatusCode())
            .body("Model training failed with message: " + trainResponse.getBody());
      }

      ResponseEntity<String> predictResponse =
          restTemplate.postForEntity(mlBaseUrl + "/score/predict", null, String.class);

      if (!predictResponse.getStatusCode().is2xxSuccessful()) {
        LOGGER.error(
            "Prediction failed with status: {} and message: {}",
            predictResponse.getStatusCode(),
            predictResponse.getBody());
        return ResponseEntity.status(predictResponse.getStatusCode())
            .body("Prediction failed with message: " + predictResponse.getBody());
      }

      LOGGER.info("Initialization complete");
      return ResponseEntity.ok(
          new JsonObject("{\"message\": \"Initialization complete\"}").toString());
    } catch (Exception e) {
      LOGGER.error("Error during initialization", e);
      return ResponseEntity.status(500)
          .body(
              new JsonObject("{\"message\": \"Initialization failed\"}").toString()
                  + e.getMessage());
    }
  }

  /**
   * Initializes the ML system including motivation scoring. This is a sequential process where each
   * step depends on the success of the previous one.
   *
   * @return ResponseEntity indicating success or failure.
   */
  @PostMapping("/initWithMotivation")
  public ResponseEntity<String> initializeWithMotivation() {
    LOGGER.info("Received request to initialize ML with motivation");
    try {
      ResponseEntity<String> cleanResponse =
          restTemplate.postForEntity(
              mlBaseUrl + "/data/clean?cleaner=data_cleaning_version4&saveFile=clean_default",
              null,
              String.class);
      if (!cleanResponse.getStatusCode().is2xxSuccessful()) {
        LOGGER.error(
            "Data cleaning failed with status: {} and message: {}",
            cleanResponse.getStatusCode(),
            cleanResponse.getBody());
        return ResponseEntity.status(cleanResponse.getStatusCode())
            .body("Data cleaning failed with message: " + cleanResponse.getBody());
      }

      ResponseEntity<String> motivationCleaningResponse =
          restTemplate.postForEntity(
              mlBaseUrl
                  + "/data/clean?cleaner=motivation_data_cleaning_version2&saveFile=motivation_default",
              null,
              String.class);
      if (!motivationCleaningResponse.getStatusCode().is2xxSuccessful()) {
        LOGGER.error(
            "Motivation data cleaning failed with status: {} and message: {}",
            motivationCleaningResponse.getStatusCode(),
            motivationCleaningResponse.getBody());
        return ResponseEntity.status(motivationCleaningResponse.getStatusCode())
            .body(
                "Motivation data cleaning failed with message: "
                    + motivationCleaningResponse.getBody());
      }

      ResponseEntity<String> trainResponse =
          restTemplate.postForEntity(
              mlBaseUrl
                  + "/training/train?modelType=stacking_model&modelName=stacking_model_default&data=clean_default",
              null,
              String.class);
      if (!trainResponse.getStatusCode().is2xxSuccessful()) {
        LOGGER.error(
            "Model training failed with status: {} and message: {}",
            trainResponse.getStatusCode(),
            trainResponse.getBody());
        return ResponseEntity.status(trainResponse.getStatusCode())
            .body("Model training failed with message: " + trainResponse.getBody());
      }

      ResponseEntity<String> motivationTrainResponse =
          restTemplate.postForEntity(
              mlBaseUrl
                  + "/training/train?modelType=stacking_model&modelName=stacking_model_motivation&data=motivation_default",
              null,
              String.class);
      if (!motivationTrainResponse.getStatusCode().is2xxSuccessful()) {
        LOGGER.error(
            "Motivation model training failed with status: {} and message: {}",
            motivationTrainResponse.getStatusCode(),
            motivationTrainResponse.getBody());
        return ResponseEntity.status(motivationTrainResponse.getStatusCode())
            .body(
                "Motivation model training failed with message: "
                    + motivationTrainResponse.getBody());
      }

      ResponseEntity<String> predictResponse =
          restTemplate.postForEntity(
              mlBaseUrl
                  + "/score/predict?modelType=stacking_model&modelName=stacking_model_default&data=clean_default&saveFile=score_default",
              null,
              String.class);
      if (!predictResponse.getStatusCode().is2xxSuccessful()) {
        LOGGER.error(
            "Prediction failed with status: {} and message: {}",
            predictResponse.getStatusCode(),
            predictResponse.getBody());
        return ResponseEntity.status(predictResponse.getStatusCode())
            .body("Prediction failed with message: " + predictResponse.getBody());
      }

      ResponseEntity<String> motivationPredictResponse =
          restTemplate.postForEntity(
              mlBaseUrl
                  + "/score/predict?modelType=stacking_model&modelName=stacking_model_motivation&data=motivation_default&saveFile=motivation_score",
              null,
              String.class);
      if (!motivationPredictResponse.getStatusCode().is2xxSuccessful()) {
        LOGGER.error(
            "Motivation prediction failed with status: {} and message: {}",
            motivationPredictResponse.getStatusCode(),
            motivationPredictResponse.getBody());
        return ResponseEntity.status(motivationPredictResponse.getStatusCode())
            .body(
                "Motivation prediction failed with message: "
                    + motivationPredictResponse.getBody());
      }

      LOGGER.info("Initialization with motivation complete");

      return ResponseEntity.ok(
          new JsonObject("{\"message\": \"Initialization with motivation complete\"}").toString());

    } catch (Exception e) {
      LOGGER.error("Error during initialization", e);
      return ResponseEntity.status(500)
          .body(
              new JsonObject("{\"message\": \"Initialization failed\"}").toString()
                  + e.getMessage());
    }
  }

  /**
   * Cleans the data specified by the user. It can take a data file, a cleaner, and a save file as
   * optional parameters. The cleaner is a specific data cleaning method from the /data_handling
   * endpoint.
   *
   * @param data The data file to be cleaned.
   * @param dataCleaner The cleaner to be used for data cleaning.
   * @param saveFile The name of the file where the cleaned data will be saved.
   * @return ResponseEntity indicating success or failure.
   */
  @PostMapping("/clean")
  public ResponseEntity<String> cleanData(
      @RequestParam(required = false) Optional<String> data,
      @RequestParam(required = false) Optional<String> dataCleaner,
      @RequestParam(required = false) Optional<String> saveFile) {
    LOGGER.info(
        "Received request to clean data with data: {}, cleaner: {}, saveFile: {}",
        data.orElse("default"),
        dataCleaner.orElse("default"),
        saveFile.orElse("default"));

    try {
      // Construct the URL with optional parameters
      UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(mlBaseUrl + "/data/clean");
      data.ifPresent(d -> builder.queryParam("data", d));
      dataCleaner.ifPresent(c -> builder.queryParam("cleaner", c));
      saveFile.ifPresent(f -> builder.queryParam("saveFile", f));

      ResponseEntity<String> response =
          restTemplate.postForEntity(builder.toUriString(), null, String.class);

      return ResponseEntity.ok(response.getBody());

    } catch (Exception e) {
      LOGGER.error("Error during data cleaning", e);
      return ResponseEntity.status(500)
          .body(new JsonObject("{\"message\": \"Data cleaning failed\"}").toString());
    }
  }

  /**
   * Trains a model based on the specified parameters. It can take a model type, model name, data,
   * and a cleaning flag as optional parameters.
   *
   * @param modelType The type of model to be trained.
   * @param modelName The name of the model to be trained.
   * @param data The data file to be used for training.
   * @param cleaning A flag indicating whether cleaning is needed.
   * @return ResponseEntity indicating success or failure.
   */
  @PostMapping("/train")
  public ResponseEntity<String> trainModel(
      @RequestParam(required = false) Optional<String> modelType,
      @RequestParam(required = false) Optional<String> modelName,
      @RequestParam(required = false) Optional<String> data,
      @RequestParam(required = false) Optional<Boolean> cleaning) {
    LOGGER.info(
        "Received request to train model with type: {}, name: {}, data: {}, cleaning: {}",
        modelType.orElse("default"),
        modelName.orElse("default"),
        data.orElse("default"),
        cleaning.orElse(false));

    try {
      // Construct the URL with optional parameters
      UriComponentsBuilder builder =
          UriComponentsBuilder.fromUriString(mlBaseUrl + "/training/train");
      modelType.ifPresent(type -> builder.queryParam("modelType", type));
      modelName.ifPresent(name -> builder.queryParam("modelName", name));
      data.ifPresent(d -> builder.queryParam("data", d));
      cleaning.ifPresent(c -> builder.queryParam("cleaning", c));

      ResponseEntity<String> response =
          restTemplate.postForEntity(builder.toUriString(), null, String.class);

      return ResponseEntity.ok(response.getBody());

    } catch (Exception e) {
      LOGGER.error("Error during model training", e);
      return ResponseEntity.status(500)
          .body(
              new JsonObject("{\"message\": \"Model training failed\"}").toString()
                  + e.getMessage());
    }
  }

  /**
   * Placeholder for training all available ML models.
   *
   * @return ResponseEntity indicating that the functionality is not implemented.
   */
  @PostMapping("/train/all")
  public ResponseEntity<String> trainAllModels() {
    LOGGER.info("Received request to train all models");
    return ResponseEntity.ok(
        new JsonObject("{\"message\": \"Training all models is not implemented yet\"}").toString());

    // TODO: Implement when properly supported
  }

  /**
   * Placeholder for cleaning datasets using all available cleaners.
   *
   * @return ResponseEntity indicating that the functionality is not implemented.
   */
  @PostMapping("/clean/all")
  public ResponseEntity<String> cleanAllData() {
    LOGGER.info("Received request to clean data with all cleaners");
    return ResponseEntity.ok(
        new JsonObject("{\"message\": \"Cleaning with all cleaners is not implemented yet\"}")
            .toString());

    // TODO: Implement when properly supported
  }
}
