package uk.thepragmaticdev.common;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * Static file utilities. Contains various functions for creating, reading and
 * writing files.
 */
public final class FileUtil {

  /**
   * The file extension for an encrypted file.
   */
  public static final String ENCRYPTED_EXTENSION = ".8102";

  /**
   * Creates a new instance of a file object.
   *
   * @param filename  the path and name of the file
   * @param directory should the file be a directory
   * @param create    should the file be newly created
   * @return a new File instance
   * @throws ApplicationException if file does not exist or the filename is not a
   *                              directory if directory is true.
   */
  public static File create(String filename, boolean directory, boolean create) {
    if (!Files.exists(Paths.get(filename)) && !create) {
      throw new ApplicationException("File not found: " + filename);
    } else if (Files.isDirectory(Paths.get(filename)) && !directory) {
      throw new ApplicationException("Not a directory: " + filename);
    }
    return new File(filename);
  }

  /**
   * Creates a new instance of a file object.
   *
   * @param filename the path and name of the file
   * @return a new File instance
   */
  public static File create(String filename) {
    return create(filename, false, false);
  }

  /**
   * Deletes a file.
   *
   * @param file the file to delete
   */
  public static void delete(File file) {
    file.delete();
  }

  /**
   * Reads data from file.
   *
   * @param file the file to read from
   * @return the data as a byte array
   * @throws IOException if reading file fails
   */
  public static byte[] read(File file) throws IOException {
    return Files.readAllBytes(file.toPath());
  }

  /**
   * Writes the given data to the specified file.
   *
   * @param file    the file to write to
   * @param data    the data to write to file
   * @param options optional standard options for writing
   * @throws IOException if writing to file fails
   */
  public static void write(File file, byte[] data, StandardOpenOption... options) throws IOException {
    Files.write(file.toPath(), data, options);
  }

  /**
   * Reads a password from the console. Input will be hidden from the user.
   *
   * @return the password as a byte array
   */
  public static byte[] readPassword() {
    char[] password = System.console().readPassword("Enter your password: ");
    ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(password));
    return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
  }

  /**
   * Checks if the given file is encrypted.
   *
   * @param file the file to check
   * @return true if the file extension ends with the encrypted extension.
   */
  public static boolean isEncrypted(File file) {
    return file.getName().endsWith(ENCRYPTED_EXTENSION);
  }
}