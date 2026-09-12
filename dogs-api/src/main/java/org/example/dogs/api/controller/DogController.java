package org.example.dogs.api.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.service.DogService;

@Controller("/api/dogs")
public class DogController {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @Post
    public Dog createDog(@Body Dog dog) {
        return dogService.createDog(dog);
    }

    @Get("/{id}")
    public Dog getDog(Long id) {
        return dogService.getDog(id);
    }
}