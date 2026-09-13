package org.example.dogs.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpResponse;
import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.query.DogFilter;
import org.example.dogs.domain.query.DogPage;
import org.example.dogs.domain.query.DogPageRequest;
import org.example.dogs.domain.service.DogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DogControllerTest {

    private DogService dogService;
    private DogController controller;

    @BeforeEach
    void setUp() {
        dogService = mock(DogService.class);
        controller = new DogController(dogService, new ObjectMapper());
    }

    @Test
    void shouldCreateDog() {
        Dog dog = new Dog(null, "Rex", "German Shepherd");
        Dog createdDog = new Dog(1L, "Rex", "German Shepherd");

        when(dogService.createDog(dog)).thenReturn(createdDog);

        HttpResponse<Dog> response = controller.createDog(dog);
        Dog result = response.body();

        assertEquals(201, response.code());
        assertEquals(createdDog, result);

        verify(dogService).createDog(dog);
    }

    @Test
    void shouldGetDogById() {
        Dog dog = new Dog(1L, "Rex", "German Shepherd");

        when(dogService.getDog(1L)).thenReturn(dog);

        Dog result = controller.getDog(1L);

        assertEquals(dog, result);

        verify(dogService).getDog(1L);
    }

    @Test
    void shouldListDogs() throws JsonProcessingException {
        Dog rex = new Dog(1L, "Rex", "German Shepherd");
        Dog max = new Dog(2L, "Max", "Labrador");

        DogFilter filter = new DogFilter(null, null, null);
        DogPageRequest pageRequest = new DogPageRequest(0, 10);

        DogPage dogPage =
                new DogPage(
                        List.of(rex, max),
                        0,
                        10,
                        2,
                        1
                );

        when(dogService.listDogs(filter, pageRequest))
                .thenReturn(dogPage);

        DogPage result = controller.listDogs(0, 10, "");

        assertEquals(2, result.content().size());
        assertEquals("Rex", result.content().get(0).name());
        assertEquals("Max", result.content().get(1).name());

        verify(dogService).listDogs(filter, pageRequest);
    }

    @Test
    void shouldPassPaginationToService() throws JsonProcessingException {
        DogFilter filter = new DogFilter(null, null, null);
        DogPageRequest pageRequest = new DogPageRequest(2, 5);

        DogPage dogPage =
                new DogPage(
                        List.of(),
                        2,
                        5,
                        0,
                        0
                );

        when(dogService.listDogs(filter, pageRequest))
                .thenReturn(dogPage);

        controller.listDogs(2, 5, "");

        verify(dogService).listDogs(filter, pageRequest);
    }
}