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
import org.example.dogs.domain.model.*;
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
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

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

    @Test
    void shouldFilterDogsByBreed() {
        dogRepository.create(new Dog(null, "Rex", "German Shepherd"));
        dogRepository.create(new Dog(null, "Gia", "Lagotto Romagnolo"));

        URI uri = UriBuilder.of("/api/dogs/dogs")
                .queryParam("filter", "{\"breed\":\"Lagotto Romagnolo\"}")
                .build();

        HttpRequest<?> request = HttpRequest.GET(uri);

        HttpResponse<DogPage> response =
                client.toBlocking().exchange(request, DogPage.class);

        assertEquals(200, response.code());
        assertEquals(1, response.body().content().size());
        assertEquals("Gia", response.body().content().get(0).name());
    }

    @Test
    void shouldFilterDogsBySupplier() {
        Dog layla = new Dog(
                null,
                "Layla",
                "German Shepherd",
                null,       // badgeId
                null,       // gender
                null,       // birthDate
                null,       // dateAcquired
                null,       // currentStatus
                null,       // leavingDate
                null,       // leavingReason
                new Supplier(null, "Kevena"),
                Set.of()    // kennellingCharacteristics
        );
        Dog gia = new Dog(
                null,
                "Gia",
                "Lagotto Romagnolo",
                null,       // badgeId
                null,       // gender
                null,       // birthDate
                null,       // dateAcquired
                null,       // currentStatus
                null,       // leavingDate
                null,       // leavingReason
                new Supplier(null, "Tartu"),
                Set.of()    // kennellingCharacteristics
        );
        dogRepository.create(layla);
        dogRepository.create(gia);

        URI uri = UriBuilder.of("/api/dogs/dogs")
                .queryParam("filter", "{\"supplier\":\"Kevena\"}")
                .build();

        HttpRequest<?> request = HttpRequest.GET(uri);

        HttpResponse<DogPage> response =
                client.toBlocking().exchange(request, DogPage.class);

        assertEquals(200, response.code());
        assertEquals(1, response.body().content().size());
        assertEquals("Kevena", response.body().content().get(0).supplier().name());
    }

    @Test
    void shouldRetireDog() {
        Dog layla = dogRepository.create(new Dog(
                null,
                "Layla",
                "German Shepherd",
                null,
                null,
                null,
                null,
                DogStatus.IN_SERVICE,
                null,
                null,
                new Supplier(null, "Kevena"),
                Set.of()
        ));

        String json = """
            {
              "name": "Layla",
              "breed": "German Shepherd",
              "currentStatus": "RETIRED",
              "leavingDate": "2026-09-13",
              "leavingReason": "RETIRED_REHOUSED",
              "supplier": {
                "name": "Kevena"
              },
              "kennellingCharacteristics": []
            }
            """;

        HttpRequest<String> request =
                HttpRequest.PUT("/api/dogs/" + layla.id(), json)
                        .contentType(MediaType.APPLICATION_JSON_TYPE);

        HttpResponse<Dog> response =
                client.toBlocking().exchange(request, Dog.class);

        assertEquals(200, response.code());
        assertEquals(layla.id(), response.body().id());
        assertEquals("Layla", response.body().name());
        assertEquals(DogStatus.RETIRED, response.body().currentStatus());
        assertEquals(LocalDate.of(2026, 9, 13), response.body().leavingDate());
        assertEquals(LeavingReason.RETIRED_REHOUSED, response.body().leavingReason());
    }
}