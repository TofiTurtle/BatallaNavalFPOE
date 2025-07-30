package com.example.batallanavalfpoe.model;

/**
 * Abstraction for serializing and deserializing objects to and from a file.
 * <p>
 * Implementations should define the underlying format (e.g., Java native serialization,
 * JSON, binary) and clearly document any assumptions such as required interfaces,
 * versioning strategy, and charset (if text-based).
 * </p>
 *
 * <p><strong>Error handling:</strong> Since this API does not declare checked exceptions,
 * implementations should catch I/O and format-related checked exceptions and rethrow them
 * as unchecked exceptions with meaningful messages.</p>
 *
 * <p><strong>Thread-safety:</strong> Implementations are not required to be thread-safe
 * unless explicitly stated.</p>
 *
 * @since 1.0
 */
public interface ISerializableFileHandler {

    /**
     * Serializes the provided object and writes it to the target file.
     * <p>
     * Implementations should document any requirements for {@code element}
     * (e.g., must implement {@code java.io.Serializable}) and whether the write
     * operation overwrites existing files or creates parent directories.
     * </p>
     *
     * @param filename path to the destination file
     * @param element  object instance to serialize
     * @throws RuntimeException if the serialization or write operation fails
     */
    void serialize(String filename, Object element);

    /**
     * Reads the specified file and deserializes its contents into an object.
     * <p>
     * The concrete return type depends on the implementation and the stored data.
     * Callers are expected to cast the returned value to the expected type.
     * Implementations should document the expected type and any versioning
     * or compatibility constraints.
     * </p>
     *
     * @param filename path to the source file
     * @return the deserialized object instance (may be {@code null} if the format supports it)
     * @throws RuntimeException if the read or deserialization operation fails
     */
    Object deserialize(String filename);
}
