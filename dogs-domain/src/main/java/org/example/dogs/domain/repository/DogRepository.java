package org.example.dogs.domain.repository;

import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.query.DogFilter;
import org.example.dogs.domain.query.DogPage;
import org.example.dogs.domain.query.DogPageRequest;

import java.util.Optional;

public interface DogRepository {

    Optional<Dog> findById(Long id);

    Dog create(Dog dog);

    Dog update(Dog dog);

    void softDelete(Long id);

    DogPage findAll(DogFilter filter, DogPageRequest pageRequest);
}
