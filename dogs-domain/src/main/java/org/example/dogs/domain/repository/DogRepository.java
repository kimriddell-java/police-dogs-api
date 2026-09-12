package org.example.dogs.domain.repository;

import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.model.DogFilter;
import org.example.dogs.domain.model.DogPage;
import org.example.dogs.domain.model.DogPageRequest;

import java.util.Optional;

public interface DogRepository {

    Optional<Dog> findById(Long id);

    Dog create(Dog dog);

    Dog update(Dog dog);

    void softDelete(Long id);

    DogPage findAll(DogFilter filter, DogPageRequest pageRequest);
}
