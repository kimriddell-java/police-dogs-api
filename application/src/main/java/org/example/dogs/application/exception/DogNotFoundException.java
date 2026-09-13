package org.example.dogs.application.exception;

public class DogNotFoundException extends RuntimeException {

    public DogNotFoundException(Long id) {
        super("Dog not found: " + id);
    }
}