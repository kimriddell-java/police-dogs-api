package org.example.dogs.adapter.in.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import jakarta.validation.Valid;
import org.example.dogs.application.port.in.DogService;
import org.example.dogs.application.query.DogFilter;
import org.example.dogs.model.Dog;
import org.example.dogs.model.DogStatus;
import org.example.dogs.model.Gender;
import org.example.dogs.model.LeavingReason;
import org.example.dogs.application.query.DogPage;
import org.example.dogs.application.query.DogPageRequest;

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
    public HttpResponse<Dog> createDog(@Body @Valid DogRequest request) {
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
    public Dog updateDog(Long id, @Body @Valid DogRequest request) {
        Dog dog = dogApiMapper.toDomain(request, id);
        return dogService.updateDog(id, dog);
    }

    @Delete("/{id}")
    public HttpResponse<?> deleteDog(Long id) {
        dogService.deleteDog(id);
        return HttpResponse.noContent();
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

    @Get("/statuses")
    public DogStatus[] getStatuses() {
        return DogStatus.values();
    }

    @Get("/leaving-reasons")
    public LeavingReason[] getLeavingReasons() {
        return LeavingReason.values();
    }

    @Get("/genders")
    public Gender[] getGenders() {
        return Gender.values();
    }
}