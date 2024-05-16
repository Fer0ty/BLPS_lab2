package ru.artemiyandarina.blps_lab2.exceptions.repository;

// Клас ошибки, которая возникает при некорректном использовании методов класса FileRepository.
// Ошибка является необрабатываемой, следовательно для ее устранения нужно изменить исходный код.
public class FileRepositoryMethodException extends RuntimeException {
    public FileRepositoryMethodException(String message) {
        super(message);
    }
}
