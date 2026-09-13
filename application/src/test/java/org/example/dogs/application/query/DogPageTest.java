package org.example.dogs.application.query;

import org.example.dogs.model.Dog;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DogPageTest {

    @Test
    void shouldCalculateTotalPagesAndHandlePaginationMetadata() {
        Dog dog1 = new Dog(1L, "Rex", "German Shepherd");
        Dog dog2 = new Dog(2L, "Max", "Labrador");

        DogPageRequest request = new DogPageRequest(0, 2);
        DogPage page = DogPage.of(List.of(dog1, dog2), request, 5);

        assertEquals(0, page.page());
        assertEquals(2, page.size());
        assertEquals(5, page.totalElements());
        assertEquals(3, page.totalPages());
        assertEquals(2, page.content().size());
    }

    @Test
    void shouldHandleEmptyContentAndZeroTotalElements() {
        DogPageRequest request = new DogPageRequest(0, 10);
        DogPage page = DogPage.of(null, request, 0);

        assertNotNull(page.content());
        assertTrue(page.content().isEmpty());
        assertEquals(0, page.totalElements());
        assertEquals(0, page.totalPages());
    }

    @Test
    void shouldDefaultPageRequestOnInvalidInput() {
        DogPageRequest request = new DogPageRequest(-1, -5);
        assertEquals(0, request.page());
        assertEquals(10, request.size());
    }

    @Test
    void shouldCreateDefaultDogFilter() {
        DogFilter filter = new DogFilter();
        assertEquals(null, filter.name());
        assertEquals(null, filter.breed());
        assertEquals(null, filter.supplier());
    }
}
