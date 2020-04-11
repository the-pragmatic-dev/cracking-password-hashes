package uk.thepragmaticdev.common;

/**
 * A general catch all application exception.
 */
public final class ApplicationException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message the error message
   */
  public ApplicationException(String message) {
    super(message);
  }
}