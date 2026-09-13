package org.example.dogs.bootstrap;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.example.dogs.application.port.in.DogService;
import org.example.dogs.application.port.out.persistence.DogRepository;
import org.example.dogs.application.service.DogServiceImpl;

@Factory
public class ApplicationFactory {

    @Singleton
    DogService dogService(DogRepository dogRepository) {
        return new DogServiceImpl(dogRepository);
    }
}