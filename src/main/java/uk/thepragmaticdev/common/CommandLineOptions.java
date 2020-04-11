package uk.thepragmaticdev.common;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * A command line options class responsible for holding all valid options for
 * the application. This avoids the need to manually parse the supplied args.
 */
public final class CommandLineOptions {

  /**
   * Apache Commons Command Line to parse for options and data.
   */
  private CommandLine cmd;

  /**
   * The arguments passed to the application.
   */
  private final String[] args;

  /**
   * An Options object to hold all valid application options.
   */
  private final Options options;

  /**
   * Constructor.
   *
   * @param args the command line arguments
   */
  public CommandLineOptions(String[] args) {
    this.args = args;
    this.options = new Options();
  }

  /**
   * Checks if a given option exists.
   *
   * @param option the option
   * @return true if the option exists, otherwise false
   */
  public boolean hasOption(char option) {
    return cmd.hasOption(option);
  }

  /**
   * Returns an options argument value.
   *
   * @param option the option
   * @return the value of the option
   */
  public String getOptionValue(char option) {
    return cmd.getOptionValue(option);
  }

  /**
   * Adds a new command line option.
   *
   * @param option      the short option
   * @param longOption  the long option
   * @param hasArg      does the option have an argument
   * @param description the options description
   */
  public void addOption(String option, String longOption, boolean hasArg, String description) {
    options.addOption(option, longOption, hasArg, description);
  }

  /**
   * Builds the command line ready for reading.
   *
   * @throws ParseException if the arguments are incorrectly formed
   */
  public void build() throws ParseException {
    this.cmd = new BasicParser().parse(options, args);
  }

  /**
   * Prints all available command line options to the user.
   *
   * @param cmdLineSyntax the syntax to run the application
   */
  public void printHelp(String cmdLineSyntax) {
    new HelpFormatter().printHelp(cmdLineSyntax, options);
  }
}