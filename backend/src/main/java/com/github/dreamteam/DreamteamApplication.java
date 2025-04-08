/** The main package for the Dreamteam application. */
package com.github.dreamteam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class for the Dreamteam application. It serves as the entry point for the Spring Boot
 * application.
 */
@SpringBootApplication
public class DreamteamApplication {

  /**
   * The main method that starts the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(DreamteamApplication.class, args);
  }
}
