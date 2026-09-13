package org.example.dogs.application;

import io.micronaut.data.connection.annotation.Connectable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.model.DogStatus;
import org.example.dogs.domain.model.Gender;
import org.example.dogs.domain.query.DogPage;
import org.example.dogs.domain.repository.DogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.net.URI;
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

    @Inject
    DataSource dataSource;

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

    @SuppressWarnings("SqlResolve")
    @BeforeEach
    @Connectable
    void cleanDatabase() throws Exception {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM dog_kennelling_characteristic");
            statement.executeUpdate("DELETE FROM dog");
            statement.executeUpdate("DELETE FROM kennelling_characteristic");
            statement.executeUpdate("DELETE FROM supplier");
        }
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

    @Test
    void shouldCreateDog() {
        String json = """
                {
                  "name": "Rex",
                  "breed": "German Shepherd",
                  "badgeId": "PD-1234",
                  "gender": "MALE",
                  "birthDate": "2022-03-15",
                  "dateAcquired": "2023-01-10",
                  "currentStatus": "IN_SERVICE",
                  "supplier": {
                    "name": "Westfield Kennels"
                  },
                  "kennellingCharacteristics": [
                    {
                      "name": "Needs individual kennel"
                    },
                    {
                      "name": "High energy"
                    }
                  ]
                }
                """;

        HttpRequest<String> request =
                HttpRequest.POST("/api/dogs", json)
                        .contentType(MediaType.APPLICATION_JSON);

        HttpResponse<Dog> response =
                client.toBlocking().exchange(request, Dog.class);

        assertEquals(201, response.code());
        assertEquals("Rex", response.body().name());
        assertEquals("German Shepherd", response.body().breed());
        assertEquals("PD-1234", response.body().badgeId());
        assertEquals(Gender.MALE, response.body().gender());
        assertEquals(DogStatus.IN_SERVICE, response.body().currentStatus());
        assertEquals("Westfield Kennels", response.body().supplier().name());
        assertEquals(2, response.body().kennellingCharacteristics().size());
    }

    @Test
    void shouldListDogs() {
        dogRepository.create(new Dog(null, "Rex", "German Shepherd"));
        dogRepository.create(new Dog(null, "Max", "Labrador"));

        HttpRequest<?> request =
                HttpRequest.GET("/api/dogs/dogs");

        HttpResponse<DogPage> response =
                client.toBlocking().exchange(request, DogPage.class);

        assertEquals(200, response.code());
        assertEquals(2, response.body().content().size());
    }

    @Test
    void shouldPaginateDogs() {
        dogRepository.create(new Dog(null, "Rex", "German Shepherd"));
        dogRepository.create(new Dog(null, "Max", "Labrador"));

        HttpRequest<?> request =
                HttpRequest.GET("/api/dogs/dogs?page=0&size=1");

        HttpResponse<DogPage> response =
                client.toBlocking().exchange(request, DogPage.class);

        assertEquals(200, response.code());
        assertEquals(1, response.body().content().size());
        assertEquals(2, response.body().totalElements());
        assertEquals(0, response.body().page());
        assertEquals(1, response.body().size());
    }

    @Test
    void shouldFilterDogsByName() {
        dogRepository.create(new Dog(null, "Rex", "German Shepherd"));
        dogRepository.create(new Dog(null, "Max", "Labrador"));

        URI uri = UriBuilder.of("/api/dogs/dogs")
                .queryParam("filter", "{\"name\":\"Rex\"}")
                .build();

        HttpRequest<?> request = HttpRequest.GET(uri);

        HttpResponse<DogPage> response =
                client.toBlocking().exchange(request, DogPage.class);

        assertEquals(200, response.code());
        assertEquals(1, response.body().content().size());
        assertEquals("Rex", response.body().content().get(0).name());
    }
}