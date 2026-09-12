package org.example.dogs.api.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.example.dogs.api.dto.DogResponse;
import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.service.DogService;

@Controller("/api/dogs")
public class DogController {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @Get("/{id}")
    public DogResponse getDog(Long id) {

        Dog dog = dogService.getDog(id);

        return new DogResponse(
                dog.id(),
                dog.name(),
                dog.breed()
        );
    }
}