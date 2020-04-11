package uk.thepragmaticdev;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.thepragmaticdev.common.ApplicationException;
import uk.thepragmaticdev.common.FileUtil;

/**
 * A class to crack a list of hashed passwords using a given dictionary.
 */
public final class Recover {

  /**
   * Logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(Recover.class);

  /**
   * Dictionary to retrieve potential passwords.
   */
  private final Dictionary dictionary;

  /**
   * Constructor.
   *
   * @param dictionaryDirectory the path of the dictionary directory
   */
  public Recover(File dictionaryDirectory) {
    this.dictionary = new Dictionary(dictionaryDirectory);
  }

  /**
   * Attempts to crack all of the hashes contained in the hashed file and outputs
   * the results to a file. The results file contains the original hash and the
   * plaintext password.
   *
   * @param hashedFile the file containing the hashes to crack
   * @param outputFile the file to write the results to
   * @throws ApplicationException if there's an error reading file
   */
  public void crack(File hashedFile, File outputFile) {
    try {
      FileUtil.write(outputFile, new byte[0]);
      // Create initial metrics for cracking performance
      Metrics metrics = new Metrics(System.currentTimeMillis());
      List<String> hashes = Files.lines(hashedFile.toPath()).collect(Collectors.toList());
      int initialHashesSize = hashes.size();

      // Loop until the dictionary has been exhausted or all hashes have been cracked
      while (dictionary.hasNext() && !hashes.isEmpty()) {
        String password = dictionary.next();
        byte[] crackedHash = generateHash(password);

        // Check every hash against the crackedHash
        Iterator<String> iter = hashes.iterator();
        while (iter.hasNext()) {
          String row = iter.next();
          byte[] hash = DatatypeConverter.parseHexBinary(row);

          // If the hashes match we cracked the password
          if (Arrays.equals(hash, crackedHash)) {
            // Write the hash and correct password to the output file
            String output = DatatypeConverter.printHexBinary(hash) + " " + password + "\r\n";
            FileUtil.write(outputFile, output.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            iter.remove();
            metrics.incrementSolved();
          }
        }
        metrics.incrementAttempts();
      }
      logger.info("{} hash(s) analysed, {} password(s) found.\nExecution time: {} secs.\nAttempts: {}",
          initialHashesSize, metrics.getSolved(), metrics.getExecutionTime(), metrics.getAttempts());
    } catch (IOException ex) {
      throw new ApplicationException("Error reading file");
    }
  }

  /**
   * Generates a SHA-256 hash.
   *
   * @param password the plaintext password to hash
   * @return the hashed password
   * @throws ApplicationException if there's an error hashing the password
   */
  private byte[] generateHash(String password) {
    try {
      return MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException ex) {
      throw new ApplicationException("Error hashing password");
    }
  }
}