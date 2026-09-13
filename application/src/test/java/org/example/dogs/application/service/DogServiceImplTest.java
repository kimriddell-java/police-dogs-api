package org.example.dogs.application.service;

import org.example.dogs.application.port.out.persistence.DogRepository;
import org.example.dogs.application.query.DogFilter;
import org.example.dogs.application.query.DogPage;
import org.example.dogs.application.query.DogPageRequest;
import org.example.dogs.model.Dog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DogServiceImplTest {

    @Test
    void shouldCreateDog() {
        DogRepository dogRepository = Mockito.mock(DogRepository.class);
        DogServiceImpl dogService = new DogServiceImpl(dogRepository);

        Dog dog = new Dog(null, "Rex", "German Shepherd");
        Dog createdDog = new Dog(1L, "Rex", "German Shepherd");

        Mockito.when(dogRepository.create(dog)).thenReturn(createdDog);

        Dog result = dogService.createDog(dog);

        Assertions.assertEquals(createdDog, result);
        Mockito.verify(dogRepository).create(dog);
    }

    @Test
    void shouldGetDogById() {
        DogRepository dogRepository = Mockito.mock(DogRepository.class);
        DogServiceImpl dogService = new DogServiceImpl(dogRepository);

        Dog dog = new Dog(1L, "Rex", "German Shepherd");

        Mockito.when(dogRepository.findById(1L)).thenReturn(Optional.of(dog));

        Dog result = dogService.getDog(1L);

        Assertions.assertEquals(dog, result);
    }

    @Test
    void shouldUpdateDog() {
        DogRepository dogRepository = Mockito.mock(DogRepository.class);
        DogServiceImpl dogService = new DogServiceImpl(dogRepository);

        Dog dog = new Dog(1L, "Rex", "German Shepherd");

        Mockito.when(dogRepository.update(dog)).thenReturn(dog);

        Dog result = dogService.updateDog(1L, dog);

        Assertions.assertEquals(dog, result);
    }

    @Test
    void shouldDeleteDog() {
        DogRepository dogRepository = Mockito.mock(DogRepository.class);
        DogServiceImpl dogService = new DogServiceImpl(dogRepository);

        dogService.deleteDog(1L);

        Mockito.verify(dogRepository).softDelete(1L);
    }

    @Test
    void shouldListDogs() {
        DogRepository dogRepository = Mockito.mock(DogRepository.class);
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

        Mockito.when(dogRepository.findAll(filter, pageRequest)).thenReturn(expectedPage);

        DogPage result = dogService.listDogs(filter, pageRequest);

        Assertions.assertEquals(expectedPage, result);
    }
}