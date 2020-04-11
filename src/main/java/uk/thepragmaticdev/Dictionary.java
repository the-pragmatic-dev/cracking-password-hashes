package uk.thepragmaticdev;

import static uk.thepragmaticdev.Dictionary.Stage.COMPLETE;
import static uk.thepragmaticdev.Dictionary.Stage.STAGE_FIVE;
import static uk.thepragmaticdev.Dictionary.Stage.STAGE_FOUR;
import static uk.thepragmaticdev.Dictionary.Stage.STAGE_ONE;
import static uk.thepragmaticdev.Dictionary.Stage.STAGE_THREE;
import static uk.thepragmaticdev.Dictionary.Stage.STAGE_TWO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

import uk.thepragmaticdev.common.ApplicationException;

/**
 * A class which reads potential passwords from files and generates permutations
 * of given passwords and random alphanumerical characters.
 */
public final class Dictionary {

  /**
   * Enumeration to state which stage the dictionary is at. The higher stages take
   * longer to compute passwords.
   */
  enum Stage {
    STAGE_ONE, STAGE_TWO, STAGE_THREE, STAGE_FOUR, STAGE_FIVE, COMPLETE
  }

  /**
   * The directory containing various password files.
   */
  private final File dictionaryDirectory;

  /**
   * An array to store all alphanumerical characters and numbers.
   */
  private final char[] alphabet;

  /**
   * The current stage of the dictionary.
   */
  private Stage currentStage;

  /**
   * A queue of potential passwords to hash.
   */
  private Queue<String> passwords;

  /**
   * A cached queue of raw passwords read from file. Avoids the need to read from
   * file multiple times.
   */
  private Queue<String> cachedPasswords;

  /**
   * A queue of permuted passwords. For example, permutations of sophie are
   * sOphie, SOpHIe etc. For optimisation, we calculate all permutations at once
   * then exhaust all further possibilities of each before moving on to the next.
   */
  private Queue<String> passwordPermutations;

  /**
   * Flag to avoid generating the short alphanumerical passwords multiple times.
   */
  private boolean generatedAlphanumericalPasswords;

  /**
   * Constructor. Initiates alphabet, sets current stage to one and updates
   * passwords with a small list of passwords to begin with.
   *
   * @param dictionaryDirectory the directory of password files
   */
  public Dictionary(File dictionaryDirectory) {
    this.dictionaryDirectory = dictionaryDirectory;
    // Post optimisation. We know two passwords start with 'K' and '@', so put these in the alphabet
    // first as an experiment to show the danger of an attacker only knowing the starting character
    // of a password (perhaps seen through shoulder surfing)!
    this.alphabet = "K@_!#$%^&*ABCDEFGHIJLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    this.currentStage = STAGE_ONE;
    this.passwords = new LinkedList<>();
    this.cachedPasswords = new LinkedList<>();
    this.passwordPermutations = new LinkedList<>();
    updatePasswords();
  }

  /**
   * Retrieves and removes the next password in the queue.
   *
   * @return a new password
   */
  public String next() {
    return this.passwords.poll();
  }

  /**
   * Checks if a password is available. If not, it will try to update the list of
   * passwords.
   *
   * @return true if a password is available
   */
  public boolean hasNext() {
    if (passwords.peek() == null) {
      updatePasswords();
    }
    return passwords.peek() != null;
  }

  /**
   * Updates the queue with new passwords depending the current stage. Each stage
   * gets more complicated and takes longer to generate. It first starts by
   * reading simple lists of passwords from boy and girl names before moving on to
   * randomly generating passwords.
   */
  private void updatePasswords() {
    switch (currentStage) {
      case STAGE_ONE:
        this.passwords = loadPasswords("/girl_names.txt");
        this.currentStage = STAGE_TWO;
        break;
      case STAGE_TWO:
        this.passwords = loadPasswords("/boy_names.txt");
        this.currentStage = STAGE_THREE;
        break;
      case STAGE_THREE:
        generateCaseSensitiveNamesWithFourDigits();
        if (!this.passwords.isEmpty()) {
          break;
        }
        this.currentStage = STAGE_FOUR;
      case STAGE_FOUR:
        this.passwords = loadPasswords("/word_list_moby_all_moby_words.flat.txt");
        this.currentStage = STAGE_FIVE;
        break;
      case STAGE_FIVE:
        generateAlphanumericalPasswords();
        if (!this.passwords.isEmpty()) {
          break;
        }
        this.currentStage = COMPLETE;
        break;
    }
  }

  /**
   * Load a queue of passwords from file. We also cache these raw passwords so we
   * don't need to read from the same file again.
   *
   * @param passwordFile the name of the password file
   * @return a queue of passwords loaded from file
   * @throws ApplicationException if there's an error reading file
   */
  private Queue<String> loadPasswords(String passwordFile) {
    try {
      File file = new File(dictionaryDirectory.getPath().concat(passwordFile));
      Queue<String> passwords = Files.lines(file.toPath()).collect(Collectors.toCollection(LinkedList::new));
      this.cachedPasswords.addAll(passwords);
      return passwords;
    } catch (IOException ex) {
      throw new ApplicationException(ex.getMessage());
    }
  }

  /**
   * Generates a queue of case sensitive passwords each with a four digit number
   * appended. The possible number of combinations for a name is 2^n, where n
   * equals the length of the name. To avoid memory issues, one call batches
   * 10,000 results of a permutation e.g. sOphie1, sOphie2 etc.
   */
  private void generateCaseSensitiveNamesWithFourDigits() {
    this.passwords.clear();

    if (passwordPermutations.isEmpty()) {
      // Create more permutations of a name e.g. sophie s0pHie
      if (this.cachedPasswords.peek() != null) {
        String name = this.cachedPasswords.poll().toLowerCase();

        int combinations = 1 << name.length();
        for (int i = 0; i < combinations; i++) {
          char[] result = name.toCharArray();
          for (int j = 0; j < name.length(); j++) {
            if (((i >> j) & 1) == 1) {
              result[j] = Character.toUpperCase(name.charAt(j));
            }
          }
          this.passwordPermutations.add(new String(result));
        }
        // Then create 10000 variations of a permutation by appending a number of 0-9999
        appendFourDigitNumbers(passwordPermutations.poll());
      }
    } else {
      // Create 10000 variations of a permutation by appending a number of 0-9999
      appendFourDigitNumbers(passwordPermutations.poll());
    }
  }

  /**
   * Takes a permutation (e.g. sOphIe) and iterates through 0-9999 appending the
   * number to it. Add these to the next password batch.
   */
  private void appendFourDigitNumbers(String permutation) {
    for (int i = 0; i < 10000; i++) {
      this.passwords.add(permutation.concat(String.valueOf(i)));
    }
  }

  /**
   * Generates a 71 set of four-letter passwords for each alphanumerical
   * permutation.
   */
  private void generateAlphanumericalPasswords() {
    // Called once to create the initial 71^3 passwords
    if (!generatedAlphanumericalPasswords) {
      this.generatedAlphanumericalPasswords = true;
      generateShortAlphanumericalPasswords(0, "");
    }

    if (this.passwordPermutations.peek() != null) {
      // Create a batch of 71 passwords for the given permutation
      String permutation = this.passwordPermutations.poll();
      for (char letter : alphabet) {
        this.passwords.add(permutation.concat(String.valueOf(letter)));
      }
    }
  }

  /**
   * Generates three-letter password permutations from the global alphabet. As the
   * alphabet is 71 characters long, we only create three letter passwords given
   * us a complexity of 71^3 rather than 71^4. We add the last letter when
   * creating the password batch.
   *
   * @param size        the current size of the permutation
   * @param permutation the current password permutation
   */
  private void generateShortAlphanumericalPasswords(int size, String permutation) {
    if (size == 3) {
      this.passwordPermutations.add(permutation);
    } else {
      for (char letter : this.alphabet) {
        generateShortAlphanumericalPasswords(size + 1, permutation.concat(String.valueOf(letter)));
      }
    }
  }
}