package org.example.dogs.application.service;

import org.example.dogs.application.exception.DogNotFoundException;
import org.example.dogs.application.port.in.DogService;
import org.example.dogs.application.port.out.persistence.DogRepository;
import org.example.dogs.application.query.DogFilter;
import org.example.dogs.application.query.DogPage;
import org.example.dogs.application.query.DogPageRequest;

import org.example.dogs.model.Dog;

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
                .orElseThrow(() ->
                        new DogNotFoundException(id)
                );
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