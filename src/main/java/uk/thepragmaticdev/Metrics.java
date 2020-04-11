package uk.thepragmaticdev;

/**
 * A class to store metric data such as number of hashes solved and
 * executionTime.
 */
public final class Metrics {

  /**
   * The amount of hashes solved.
   */
  private int solved;
  /**
   * The amount of attempts at solving hashes.
   */
  private int attempts;
  /**
   * The start time of the metrics in milliseconds.
   */
  private final long startTimeMillis;

  /**
   * Constructor.
   * 
   * @param startTimeMillis the start time in milliseconds
   */
  public Metrics(long startTimeMillis) {
    this.startTimeMillis = startTimeMillis;
  }

  /**
   * Increments the number of solved hashes by one.
   */
  public void incrementSolved() {
    this.solved++;
  }

  /**
   * Increments the number of attempts by one.
   */
  public void incrementAttempts() {
    this.attempts++;
  }

  /**
   * Returns the number of solved hashes.
   *
   * @return the solved
   */
  public int getSolved() {
    return solved;
  }

  /**
   * Returns the attempt count.
   *
   * @return the attempts
   */
  public int getAttempts() {
    return attempts;
  }

  /**
   * Returns the expired time from object creation to now.
   *
   * @return the expired time in seconds
   */
  public double getExecutionTime() {
    return ((double) (System.currentTimeMillis() - startTimeMillis)) / 1000;
  }
}