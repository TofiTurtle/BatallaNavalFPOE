package com.example.batallanavalfpoe.model;

/**
 * Abstraction for reading and writing plain text files.
 * <p>
 * Implementations should define the I/O strategy (e.g., buffering, charset)
 * and error handling approach. Methods are expected to perform blocking I/O.
 * </p>
 *
 * <p><strong>Thread-safety:</strong> Implementations are not required to be thread-safe
 * unless explicitly stated.</p>
 */
public interface IPlainTextFileHandler {

    /**
     * Writes the provided text content to a file at the given path.
     * <p>
     * Implementations should document the character encoding used (e.g., UTF-8)
     * and whether the method overwrites or appends when the file already exists.
     * </p>
     *
     * @param filePath absolute or relative path to the destination file
     * @param content  text content to write
     * @throws RuntimeException if the write operation fails (e.g., permissions, disk issues);
     *                          implementations may throw more specific unchecked exceptions
     */
    void writeToFile(String filePath, String content);

    /**
     * Reads a plain text file and returns its contents split into lines.
     * <p>
     * The line-splitting convention (e.g., {@code \n}, {@code \r\n}) should be documented
     * by the implementation. The returned array preserves the order of lines as they appear
     * in the file.
     * </p>
     *
     * @param fileName absolute or relative path to the source file
     * @return an array of lines read from the file; never {@code null}, though it may be empty
     * @throws RuntimeException if the read operation fails (e.g., file not found, permissions);
     *                          implementations may throw more specific unchecked exceptions
     */
    String[] readFromFile(String fileName);
}
