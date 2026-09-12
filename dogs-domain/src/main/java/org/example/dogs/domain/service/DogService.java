package org.example.dogs.domain.service;

import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.query.DogFilter;
import org.example.dogs.domain.query.DogPage;
import org.example.dogs.domain.query.DogPageRequest;

public interface DogService {

    Dog createDog(Dog dog);

    Dog getDog(Long id);

    Dog updateDog(Long id, Dog dog);

    void deleteDog(Long id);

    DogPage listDogs(DogFilter filter, DogPageRequest pageRequest);
}