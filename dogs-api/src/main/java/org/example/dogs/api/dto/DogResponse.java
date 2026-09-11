package org.example.dogs.api.dto;

public record DogResponse(
        Long id,
        String name,
        String breed
) {
}
