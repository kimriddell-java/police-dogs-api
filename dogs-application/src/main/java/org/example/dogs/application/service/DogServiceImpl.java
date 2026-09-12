package org.example.dogs.application.service;

import jakarta.inject.Singleton;
import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.query.DogFilter;
import org.example.dogs.domain.query.DogPage;
import org.example.dogs.domain.query.DogPageRequest;
import org.example.dogs.domain.repository.DogRepository;
import org.example.dogs.domain.service.DogService;

@Singleton
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;

    public DogServiceImpl(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    @Override
    public Dog createDog(Dog dog) {
        return dogRepository.create(dog);
    }

    @Override
    public Dog getDog(Long id) {
        return dogRepository.findById(id)
                .orElseThrow();
    }

    @Override
    public Dog updateDog(Long id, Dog dog) {
        return dogRepository.update(dog);
    }

    @Override
    public void deleteDog(Long id) {
        dogRepository.softDelete(id);
    }

    @Override
    public DogPage listDogs(DogFilter filter, DogPageRequest pageRequest) {
        return dogRepository.findAll(filter, pageRequest);
    }
}