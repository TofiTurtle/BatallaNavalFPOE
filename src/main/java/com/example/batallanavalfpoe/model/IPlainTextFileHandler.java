package com.example.batallanavalfpoe.model;

public interface IPlainTextFileHandler {
    void writeToFile(String filePath, String content);
    String[] readFromFile(String fileName);

}
