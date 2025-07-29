package com.example.batallanavalfpoe.model;

import java.io.*;

/**
 * Plain-text file handler implementation based on blocking I/O.
 * <p>
 * This implementation uses the platform default charset via {@link FileWriter} and {@link FileReader},
 * overwrites the target file on write, and prints stack traces to {@code stderr} on I/O errors
 * (checked exceptions are not rethrown).
 * </p>
 *
 * @since 1.0
 */
public class PlainTextFileHandler implements IPlainTextFileHandler {

    /**
     * Writes the provided text content to the given file path.
     * <p>
     * Behavior:
     * <ul>
     *   <li>Overwrites the file if it already exists.</li>
     *   <li>Uses the platform default charset.</li>
     *   <li>Closes resources via try-with-resources.</li>
     *   <li>On failure, prints the stack trace and returns without throwing checked exceptions.</li>
     * </ul>
     * </p>
     *
     * @param filePath absolute or relative path to the destination file
     * @param content  text content to write
     */
    @Override
    public void writeToFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a text file and returns its contents as an array of lines.
     * <p>
     * Implementation details:
     * <ul>
     *   <li>Each line is read, {@code trim()} is applied, and lines are concatenated with a comma (",") separator.</li>
     *   <li>The resulting string is then split by comma to produce the returned array.</li>
     *   <li>Empty lines become empty elements in the resulting array; a trailing separator is discarded by {@code split(",")} semantics.</li>
     *   <li>Uses the platform default charset.</li>
     *   <li>On failure, prints the stack trace and returns the result of splitting the (possibly empty) buffer.</li>
     * </ul>
     * </p>
     *
     * @param fileName absolute or relative path to the source file
     * @return array of trimmed lines, split by comma; never {@code null}
     */
    @Override
    public String[] readFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null ) {
                content.append(line.trim()).append(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString().split(",");
    }
}
