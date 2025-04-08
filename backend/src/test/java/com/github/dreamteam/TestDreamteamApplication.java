/** The main package for the TestDreamteam application. */
package com.github.dreamteam;

import org.springframework.boot.SpringApplication;

/**
 * The main class for the TestDreamteam application. It serves as the entry point for the Spring
 * Boot application.
 */
public class TestDreamteamApplication {

  /**
   * The main method that starts the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(TestDreamteamApplication.class);
    app.run(args);
  }
}
