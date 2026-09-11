package org.example.dogs.domain.repository;

import org.example.dogs.domain.model.Dog;

import java.util.Optional;

public interface DogRepository {

    Optional<Dog> findById(Long id);

    Dog create(Dog dog);

    Dog update(Dog dog);
}
