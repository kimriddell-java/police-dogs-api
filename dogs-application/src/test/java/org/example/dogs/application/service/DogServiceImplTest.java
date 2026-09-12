package org.example.dogs.application.service;

import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.query.DogFilter;
import org.example.dogs.domain.query.DogPage;
import org.example.dogs.domain.query.DogPageRequest;
import org.example.dogs.domain.repository.DogRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DogServiceImplTest {

    @Test
    void shouldCreateDog() {
        DogRepository dogRepository = mock(DogRepository.class);
        DogServiceImpl dogService = new DogServiceImpl(dogRepository);

        Dog dog = new Dog(null, "Rex", "German Shepherd");
        Dog createdDog = new Dog(1L, "Rex", "German Shepherd");

        when(dogRepository.create(dog)).thenReturn(createdDog);

        Dog result = dogService.createDog(dog);

        assertEquals(createdDog, result);
        verify(dogRepository).create(dog);
    }

    @Test
    void shouldGetDogById() {
        DogRepository dogRepository = mock(DogRepository.class);
        DogServiceImpl dogService = new DogServiceImpl(dogRepository);

        Dog dog = new Dog(1L, "Rex", "German Shepherd");

        when(dogRepository.findById(1L)).thenReturn(Optional.of(dog));

        Dog result = dogService.getDog(1L);

        assertEquals(dog, result);
    }

    @Test
    void shouldUpdateDog() {
        DogRepository dogRepository = mock(DogRepository.class);
        DogServiceImpl dogService = new DogServiceImpl(dogRepository);

        Dog dog = new Dog(1L, "Rex", "German Shepherd");

        when(dogRepository.update(dog)).thenReturn(dog);

        Dog result = dogService.updateDog(1L, dog);

        assertEquals(dog, result);
    }

    @Test
    void shouldDeleteDog() {
        DogRepository dogRepository = mock(DogRepository.class);
        DogServiceImpl dogService = new DogServiceImpl(dogRepository);

        dogService.deleteDog(1L);

        verify(dogRepository).softDelete(1L);
    }

    @Test
    void shouldListDogs() {
        DogRepository dogRepository = mock(DogRepository.class);
        DogServiceImpl dogService = new DogServiceImpl(dogRepository);

        DogFilter filter = new DogFilter("Rex", null, null);
        DogPageRequest pageRequest = new DogPageRequest(0, 10);

        DogPage expectedPage = new DogPage(
                List.of(new Dog(1L, "Rex", "German Shepherd")),
                0,
                10,
                1,
                1
        );

        when(dogRepository.findAll(filter, pageRequest)).thenReturn(expectedPage);

        DogPage result = dogService.listDogs(filter, pageRequest);

        assertEquals(expectedPage, result);
    }
}