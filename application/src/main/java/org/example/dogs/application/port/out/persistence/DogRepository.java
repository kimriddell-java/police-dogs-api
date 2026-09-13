package org.example.dogs.application.port.out.persistence;


import org.example.dogs.application.query.DogFilter;
import org.example.dogs.application.query.DogPage;
import org.example.dogs.application.query.DogPageRequest;
import org.example.dogs.model.Dog;

import java.util.Optional;

public interface DogRepository {

    Optional<Dog> findById(Long id);

    Dog create(Dog dog);

    Dog update(Dog dog);

    void softDelete(Long id);

    DogPage findAll(DogFilter filter, DogPageRequest pageRequest);
}
