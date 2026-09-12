package org.example.dogs.api.controller;

import org.example.dogs.api.dto.DogResponse;
import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.service.DogService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DogControllerTest {

    @Test
    void shouldReturnDogById() {

        DogService dogService = mock(DogService.class);

        Dog dog = new Dog(
                1L,
                "Rex",
                "German Shepherd"
        );

        when(dogService.getDog(1L)).thenReturn(dog);

        DogController controller = new DogController(dogService);

        DogResponse response = controller.getDog(1L);

        assertEquals(1L, response.id());
        assertEquals("Rex", response.name());
        assertEquals("German Shepherd", response.breed());

        verify(dogService).getDog(1L);
    }
}
