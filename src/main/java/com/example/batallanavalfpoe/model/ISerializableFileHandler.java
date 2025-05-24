package com.example.batallanavalfpoe.model;

public interface ISerializableFileHandler {
    void serialize(String filename, Object element);
    Object deserialize(String filename);
}
