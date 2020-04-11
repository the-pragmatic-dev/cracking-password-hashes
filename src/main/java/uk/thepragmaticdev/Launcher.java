package uk.thepragmaticdev;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.thepragmaticdev.common.ApplicationException;
import uk.thepragmaticdev.common.CommandLineOptions;
import uk.thepragmaticdev.common.FileUtil;

/**
 * Main launcher for the application.
 */
public class Launcher {

  /**
   * Logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

  /**
   * Applications main method. Takes a file containing a list of hashed passwords
   * and outputs a new file containing the broken hashes. Any errors will be
   * printed to console. If the options supplied are invalid print help to the
   * console.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    CommandLineOptions clo = new CommandLineOptions(args);
    clo.addOption("i", "hashes", true, "hashes path");
    clo.addOption("o", "output", true, "output filename");
    clo.addOption("d", "dictionary", true, "dictionary directory");

    Recover recover;
    try {
      clo.build();
      if (clo.hasOption('i') && clo.hasOption('o') && clo.hasOption('d')) {
        recover = new Recover(FileUtil.create(clo.getOptionValue('d'), true, false));
        recover.crack(FileUtil.create(clo.getOptionValue('i'), false, false),
            FileUtil.create(clo.getOptionValue('o'), false, true));
      } else {
        clo.printHelp("java -jar recover");
      }
    } catch (ApplicationException ex) {
      logger.error(ex.getMessage());
    } catch (ParseException ex) {
      clo.printHelp("java -jar recover");
    }
  }
}