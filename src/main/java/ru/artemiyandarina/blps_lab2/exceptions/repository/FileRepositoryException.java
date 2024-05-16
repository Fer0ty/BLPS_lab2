package ru.artemiyandarina.blps_lab2.exceptions.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Клас ошибки, которая возникает при невозможности создать файл.
@AllArgsConstructor
@Getter
public class FileRepositoryException extends Error {
    final private String message;
    final private String fullPath;
}
