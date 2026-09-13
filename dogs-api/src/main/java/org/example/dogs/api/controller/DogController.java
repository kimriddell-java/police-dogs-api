package org.example.dogs.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import org.example.dogs.api.mapper.DogApiMapper;
import org.example.dogs.api.model.DogRequest;
import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.query.DogFilter;
import org.example.dogs.domain.query.DogPage;
import org.example.dogs.domain.query.DogPageRequest;
import org.example.dogs.domain.service.DogService;

@Controller("/api/dogs")
public class DogController {

    private final DogService dogService;
    private final ObjectMapper objectMapper;
    private final DogApiMapper dogApiMapper;

    public DogController(
            DogService dogService,
            ObjectMapper objectMapper,
            DogApiMapper dogApiMapper) {
        this.dogService = dogService;
        this.objectMapper = objectMapper;
        this.dogApiMapper = dogApiMapper;
    }

    @Post
    public HttpResponse<Dog> createDog(@Body DogRequest request) {
        Dog createdDog = dogService.createDog(
                dogApiMapper.toDomain(request)
        );

        return HttpResponse.created(createdDog);
    }

    @Get("/{id}")
    public Dog getDog(Long id) {
        return dogService.getDog(id);
    }

    @Put("/{id}")
    public Dog updateDog(Long id, @Body DogRequest request) {
        Dog dog = dogApiMapper.toDomain(request, id);
        return dogService.updateDog(id, dog);
    }

    @Get("/dogs")
    public DogPage listDogs(
            @QueryValue(defaultValue = "0") int page,
            @QueryValue(defaultValue = "10") int size,
            @QueryValue(defaultValue = "") String filter)
            throws JsonProcessingException {

        DogFilter dogFilter = filter.isBlank()
                ? new DogFilter(null, null, null)
                : objectMapper.readValue(filter, DogFilter.class);

        return dogService.listDogs(
                dogFilter,
                new DogPageRequest(page, size)
        );
    }
}