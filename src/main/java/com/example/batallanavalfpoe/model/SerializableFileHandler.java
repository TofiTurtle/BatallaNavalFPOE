package com.example.batallanavalfpoe.model;

import java.io.*;

/**
 * Implementation of {@link ISerializableFileHandler} using Java's native
 * {@link ObjectOutputStream} and {@link ObjectInputStream} for binary serialization.
 * <p>
 * This class stores and retrieves full object graphs to/from a file.
 * All objects being serialized must implement {@link java.io.Serializable}.
 * </p>
 *
 * <p><strong>Error handling:</strong> This implementation catches
 * {@link IOException} and {@link ClassNotFoundException}, prints the stack trace,
 * and returns {@code null} from {@link #deserialize(String)} on failure.</p>
 *
 * @since 1.0
 */
public class SerializableFileHandler implements ISerializableFileHandler {

    /**
     * Serializes the provided object and writes it to the specified file.
     * <p>
     * Behavior:
     * <ul>
     *     <li>Overwrites the file if it already exists.</li>
     *     <li>Closes the stream automatically using try-with-resources.</li>
     *     <li>On failure, prints the stack trace without rethrowing the exception.</li>
     * </ul>
     * </p>
     *
     * @param filename path to the destination file
     * @param element  the object to serialize (must implement {@link Serializable})
     */
    @Override
    public void serialize(String filename, Object element) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(element);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes an object from the specified file.
     * <p>
     * Behavior:
     * <ul>
     *     <li>Attempts to restore the object graph from the file.</li>
     *     <li>Returns {@code null} if the file is missing, corrupted, or
     *     if the stored class is not found.</li>
     *     <li>On failure, prints the stack trace without rethrowing the exception.</li>
     * </ul>
     * </p>
     *
     * @param filename path to the source file
     * @return the deserialized object, or {@code null} if an error occurred
     */
    @Override
    public Object deserialize(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
