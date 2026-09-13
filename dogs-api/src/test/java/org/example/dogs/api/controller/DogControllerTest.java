package org.example.dogs.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dogs.api.mapper.DogApiMapper;
import org.example.dogs.api.model.DogRequest;
import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.query.DogFilter;
import org.example.dogs.domain.query.DogPage;
import org.example.dogs.domain.query.DogPageRequest;
import org.example.dogs.domain.service.DogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DogControllerTest {

    private DogService dogService;
    private DogApiMapper dogApiMapper;
    private DogController controller;

    @BeforeEach
    void setUp() {
        dogService = mock(DogService.class);
        dogApiMapper = mock(DogApiMapper.class);

        controller = new DogController(
                dogService,
                new ObjectMapper(),
                dogApiMapper
        );
    }

    @Test
    void shouldCreateDog() {
        DogRequest request = new DogRequest(
                "Rex",
                "German Shepherd",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Set.of()
        );

        Dog dog = new Dog(
                null,
                "Rex",
                "German Shepherd"
        );

        Dog createdDog = new Dog(
                1L,
                "Rex",
                "German Shepherd"
        );

        when(dogApiMapper.toDomain(request))
                .thenReturn(dog);

        when(dogService.createDog(dog))
                .thenReturn(createdDog);

        var response = controller.createDog(request);

        assertEquals(201, response.code());
        assertEquals(createdDog, response.body());

        verify(dogApiMapper).toDomain(request);
        verify(dogService).createDog(dog);
    }

    @Test
    void shouldGetDog() {
        Dog dog = new Dog(
                1L,
                "Rex",
                "German Shepherd"
        );

        when(dogService.getDog(1L))
                .thenReturn(dog);

        Dog result = controller.getDog(1L);

        assertEquals(dog, result);
        verify(dogService).getDog(1L);
    }

    @Test
    void shouldUpdateDog() {
        DogRequest request = new DogRequest(
                "Layla",
                "German Shepherd",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Set.of()
        );

        Dog mappedDog = new Dog(
                1L,
                "Layla",
                "German Shepherd"
        );

        Dog updatedDog = new Dog(
                1L,
                "Layla",
                "German Shepherd"
        );

        when(dogApiMapper.toDomain(request, 1L))
                .thenReturn(mappedDog);

        when(dogService.updateDog(1L, mappedDog))
                .thenReturn(updatedDog);

        Dog result = controller.updateDog(1L, request);

        assertEquals(updatedDog, result);

        verify(dogApiMapper).toDomain(request, 1L);
        verify(dogService).updateDog(1L, mappedDog);
    }

    @Test
    void shouldListDogs() throws Exception {
        Dog rex = new Dog(
                1L,
                "Rex",
                "German Shepherd"
        );

        Dog max = new Dog(
                2L,
                "Max",
                "Labrador"
        );

        DogFilter filter =
                new DogFilter(null, null, null);

        DogPageRequest pageRequest =
                new DogPageRequest(0, 10);

        DogPage dogPage = new DogPage(
                List.of(rex, max),
                0,
                10,
                2,
                1
        );

        when(dogService.listDogs(filter, pageRequest))
                .thenReturn(dogPage);

        DogPage result =
                controller.listDogs(0, 10, "");

        assertEquals(2, result.content().size());
        assertEquals("Rex", result.content().get(0).name());
        assertEquals("Max", result.content().get(1).name());

        verify(dogService).listDogs(filter, pageRequest);
    }

    @Test
    void shouldPassPaginationToService() throws Exception {
        DogFilter filter =
                new DogFilter(null, null, null);

        DogPageRequest pageRequest =
                new DogPageRequest(2, 5);

        DogPage dogPage = new DogPage(
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

    @Test
    void shouldParseFilter() throws Exception {
        DogFilter filter =
                new DogFilter("Rex", null, null);

        DogPageRequest pageRequest =
                new DogPageRequest(0, 10);

        DogPage dogPage = new DogPage(
                List.of(),
                0,
                10,
                0,
                0
        );

        when(dogService.listDogs(filter, pageRequest))
                .thenReturn(dogPage);

        controller.listDogs(
                0,
                10,
                "{\"name\":\"Rex\"}"
        );

        verify(dogService).listDogs(filter, pageRequest);
    }
}