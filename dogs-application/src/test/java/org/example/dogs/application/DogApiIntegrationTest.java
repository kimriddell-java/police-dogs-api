package org.example.dogs.application;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.repository.DogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(transactional = false)
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DogApiIntegrationTest implements TestPropertyProvider {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("police_dogs")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    DogRepository dogRepository;

    @Override
    public Map<String, String> getProperties() {
        if (!org.testcontainers.DockerClientFactory.instance().isDockerAvailable()) {
            return Map.of();
        }

        if (!postgres.isRunning()) {
            postgres.start();
        }

        return Map.of(
                "datasources.default.url", postgres.getJdbcUrl(),
                "datasources.default.username", postgres.getUsername(),
                "datasources.default.password", postgres.getPassword(),
                "datasources.default.driver-class-name", "org.postgresql.Driver",
                "datasources.default.schema-generate", "NONE",
                "datasources.default.dialect", "POSTGRES",
                "flyway.datasources.default.enabled", "true"
        );
    }

    @Test
    void shouldGetDogById() {
        Dog createdDog =
                dogRepository.create(new Dog(null, "Rex", "German Shepherd"));

        HttpRequest<?> request =
                HttpRequest.GET("/api/dogs/" + createdDog.id());

        HttpResponse<Dog> response =
                client.toBlocking().exchange(request, Dog.class);

        assertEquals(200, response.code());
        assertEquals(createdDog.id(), response.body().id());
        assertEquals("Rex", response.body().name());
        assertEquals("German Shepherd", response.body().breed());
    }
}