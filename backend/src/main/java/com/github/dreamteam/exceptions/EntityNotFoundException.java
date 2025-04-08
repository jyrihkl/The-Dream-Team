/**
 * This package has all the exceptions for the backend. It is responsible for handling errors and
 * exceptions that may occur during the execution of the application.
 */
package com.github.dreamteam.exceptions;

/**
 * EntityNotFoundException is thrown when a requested entity is not found in the database or
 * collection. This exception is typically used to indicate that a specific resource could not be
 * located.
 */
public class EntityNotFoundException extends RuntimeException {
  /**
   * Constructs a new EntityNotFoundException with the specified detail message.
   *
   * @param message the detail message
   */
  public EntityNotFoundException(String message) {
    super(message);
  }
}
