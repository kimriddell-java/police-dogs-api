package org.example.dogs.application.port.in;

import org.example.dogs.application.query.DogFilter;
import org.example.dogs.application.query.DogPage;
import org.example.dogs.application.query.DogPageRequest;
import org.example.dogs.model.Dog;

public interface DogService {

    Dog createDog(Dog dog);

    Dog getDog(Long id);

    Dog updateDog(Long id, Dog dog);

    void deleteDog(Long id);

    DogPage listDogs(DogFilter filter, DogPageRequest pageRequest);
}