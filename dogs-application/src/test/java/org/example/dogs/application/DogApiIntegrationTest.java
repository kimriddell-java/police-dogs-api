package org.example.dogs.application;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.example.dogs.domain.model.Dog;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
@Testcontainers(disabledWithoutDocker = true)
class DogApiIntegrationTest implements TestPropertyProvider {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Inject
    @Client("/")
    HttpClient client;

    @Override
    public Map<String, String> getProperties() {
        if (!postgres.isRunning()) {
            postgres.start();
        }

        return Map.of(
                "datasources.default.url", postgres.getJdbcUrl(),
                "datasources.default.username", postgres.getUsername(),
                "datasources.default.password", postgres.getPassword(),
                "datasources.default.driver-class-name", "org.postgresql.Driver",
                "flyway.datasources.default.enabled", "true"
        );
    }

    @Test
    void shouldGetDogById() {
        HttpRequest<?> request = HttpRequest.GET("/api/dogs/1");

        HttpResponse<Dog> response =
                client.toBlocking().exchange(request, Dog.class);

        assertEquals(200, response.code());
        assertEquals(1L, response.body().id());
    }
}