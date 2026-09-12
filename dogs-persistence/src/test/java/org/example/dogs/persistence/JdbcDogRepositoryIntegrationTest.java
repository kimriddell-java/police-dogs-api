package org.example.dogs.persistence;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.model.DogFilter;
import org.example.dogs.domain.model.DogPage;
import org.example.dogs.domain.model.DogPageRequest;
import org.example.dogs.domain.model.DogStatus;
import org.example.dogs.domain.model.Gender;
import org.example.dogs.domain.model.KennellingCharacteristic;
import org.example.dogs.domain.model.LeavingReason;
import org.example.dogs.domain.model.Supplier;
import org.example.dogs.domain.repository.DogRepository;
import org.example.dogs.persistence.entity.DogEntity;
import org.example.dogs.persistence.repository.DogDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(startApplication = false)
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcDogRepositoryIntegrationTest implements TestPropertyProvider {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("police_dogs")
            .withUsername("postgres")
            .withPassword("postgres");

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

    @Inject
    DogRepository dogRepository;

    @Inject
    DogDataRepository dogDataRepository;

    @Test
    void shouldCreateAndRetrieveRealisticDogWithSupplierAndCharacteristics() {
        Supplier supplier = new Supplier(null, "Metropolitan Police K9 Academy");
        KennellingCharacteristic char1 = new KennellingCharacteristic(null, "High Drive");
        KennellingCharacteristic char2 = new KennellingCharacteristic(null, "Dog Reactive");

        Dog newDog = new Dog(
                null,
                "Kaiser",
                "German Shepherd",
                "K9-402",
                Gender.MALE,
                LocalDate.of(2021, 3, 15),
                LocalDate.of(2022, 5, 1),
                DogStatus.IN_SERVICE,
                null,
                null,
                supplier,
                Set.of(char1, char2)
        );

        Dog created = dogRepository.create(newDog);
        assertNotNull(created);
        assertNotNull(created.id());

        Optional<Dog> retrievedOpt = dogRepository.findById(created.id());
        assertTrue(retrievedOpt.isPresent());

        Dog retrieved = retrievedOpt.get();
        assertEquals(created.id(), retrieved.id());
        assertEquals("Kaiser", retrieved.name());
        assertEquals("German Shepherd", retrieved.breed());
        assertEquals("K9-402", retrieved.badgeId());
        assertEquals(Gender.MALE, retrieved.gender());
        assertEquals(LocalDate.of(2021, 3, 15), retrieved.birthDate());
        assertEquals(LocalDate.of(2022, 5, 1), retrieved.dateAcquired());
        assertEquals(DogStatus.IN_SERVICE, retrieved.currentStatus());
        assertNull(retrieved.leavingDate());
        assertNull(retrieved.leavingReason());

        // Supplier verification
        assertNotNull(retrieved.supplier());
        assertNotNull(retrieved.supplier().id());
        assertEquals("Metropolitan Police K9 Academy", retrieved.supplier().name());

        // Kennelling characteristics verification
        assertNotNull(retrieved.kennellingCharacteristics());
        assertEquals(2, retrieved.kennellingCharacteristics().size());
        Set<String> charNames = retrieved.kennellingCharacteristics().stream()
                .map(KennellingCharacteristic::name)
                .collect(java.util.stream.Collectors.toSet());
        assertTrue(charNames.contains("High Drive"));
        assertTrue(charNames.contains("Dog Reactive"));
    }

    @Test
    void shouldSoftDeleteDogAndPreserveDatabaseRow() {
        Dog dog = new Dog(
                null,
                "Shadow",
                "Belgian Malinois",
                "K9-105",
                Gender.FEMALE,
                LocalDate.of(2019, 8, 20),
                LocalDate.of(2020, 11, 10),
                DogStatus.RETIRED,
                LocalDate.of(2026, 2, 1),
                LeavingReason.RETIRED_REHOUSED,
                new Supplier(null, "Northshire Working Dogs"),
                Set.of()
        );

        Dog created = dogRepository.create(dog);
        Long dogId = created.id();
        assertNotNull(dogId);

        // Initially retrievable via repository port
        assertTrue(dogRepository.findById(dogId).isPresent());

        // Perform soft delete
        dogRepository.softDelete(dogId);

        // findById no longer returns it
        assertFalse(dogRepository.findById(dogId).isPresent());

        // Underlying database row still exists and has deleted=true and deleted_at populated
        Optional<DogEntity> entityOpt = dogDataRepository.findById(dogId);
        assertTrue(entityOpt.isPresent());
        DogEntity entity = entityOpt.get();
        assertTrue(entity.isDeleted());
        assertNotNull(entity.getDeletedAt());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertEquals("Shadow", entity.getName());
    }

    @Test
    void shouldUpdateDogAggregateAndPreserveRecordIdentityAndAuditTimestamps() throws InterruptedException {
        Supplier initialSupplier = new Supplier(null, "Thames Valley K9 Centre");
        KennellingCharacteristic initialChar1 = new KennellingCharacteristic(null, "High Energy");
        KennellingCharacteristic initialChar2 = new KennellingCharacteristic(null, "Fence Jumper");

        Dog originalDog = new Dog(
                null,
                "Rex",
                "German Shepherd",
                "K9-100",
                Gender.MALE,
                LocalDate.of(2021, 5, 10),
                LocalDate.of(2022, 6, 1),
                DogStatus.IN_TRAINING,
                null,
                null,
                initialSupplier,
                Set.of(initialChar1, initialChar2)
        );

        Dog created = dogRepository.create(originalDog);
        Long dogId = created.id();
        assertNotNull(dogId);

        DogEntity initialEntity = dogDataRepository.findById(dogId).orElseThrow();
        var originalCreatedAt = initialEntity.getCreatedAt();
        var initialUpdatedAt = initialEntity.getUpdatedAt();
        assertNotNull(originalCreatedAt);
        assertNotNull(initialUpdatedAt);

        // Allow timestamp progression
        Thread.sleep(50);

        Supplier newSupplier = new Supplier(null, "Sussex Police Breeding Wing");
        KennellingCharacteristic newChar = new KennellingCharacteristic(null, "Requires Medication");

        Dog updatedDogPayload = new Dog(
                dogId,
                "Rex II",
                "German Shepherd",
                "K9-100-B",
                Gender.MALE,
                LocalDate.of(2021, 5, 10),
                LocalDate.of(2022, 6, 1),
                DogStatus.IN_SERVICE,
                null,
                null,
                newSupplier,
                Set.of(newChar)
        );

        Dog updated = dogRepository.update(updatedDogPayload);
        assertNotNull(updated);
        assertEquals(dogId, updated.id());
        assertEquals("Rex II", updated.name());
        assertEquals("K9-100-B", updated.badgeId());
        assertEquals(DogStatus.IN_SERVICE, updated.currentStatus());
        assertNotNull(updated.supplier());
        assertEquals("Sussex Police Breeding Wing", updated.supplier().name());
        assertEquals(1, updated.kennellingCharacteristics().size());
        assertTrue(updated.kennellingCharacteristics().stream().anyMatch(c -> "Requires Medication".equals(c.name())));

        // Verify retrieval via findById returns the updated state
        Optional<Dog> retrievedOpt = dogRepository.findById(dogId);
        assertTrue(retrievedOpt.isPresent());
        Dog retrieved = retrievedOpt.get();
        assertEquals(dogId, retrieved.id());
        assertEquals("Rex II", retrieved.name());
        assertEquals("K9-100-B", retrieved.badgeId());
        assertEquals(DogStatus.IN_SERVICE, retrieved.currentStatus());
        assertEquals("Sussex Police Breeding Wing", retrieved.supplier().name());
        assertEquals(1, retrieved.kennellingCharacteristics().size());
        assertTrue(retrieved.kennellingCharacteristics().stream().anyMatch(c -> "Requires Medication".equals(c.name())));
        assertFalse(retrieved.kennellingCharacteristics().stream().anyMatch(c -> "High Energy".equals(c.name())));
        assertFalse(retrieved.kennellingCharacteristics().stream().anyMatch(c -> "Fence Jumper".equals(c.name())));

        // Verify direct database entity: same record updated, createdAt preserved, updatedAt updated
        DogEntity postUpdateEntity = dogDataRepository.findById(dogId).orElseThrow();
        assertEquals(dogId, postUpdateEntity.getId());
        assertEquals(originalCreatedAt, postUpdateEntity.getCreatedAt());
        assertTrue(postUpdateEntity.getUpdatedAt().isAfter(initialUpdatedAt));
        assertFalse(postUpdateEntity.isDeleted());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentDog() {
        Dog nonExistent = new Dog(
                999999L,
                "Phantom",
                "Unknown",
                "K9-999",
                Gender.MALE,
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2021, 1, 1),
                DogStatus.IN_TRAINING,
                null,
                null,
                null,
                Set.of()
        );

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> dogRepository.update(nonExistent)
        );
    }

    @Test
    void shouldListAndPaginateNonDeletedDogs() {
        Supplier supplier = new Supplier(null, "Avon K9 Centre");
        Dog dog1 = dogRepository.create(new Dog(null, "Apollo", "German Shepherd", "K9-1", Gender.MALE, LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1), DogStatus.IN_SERVICE, null, null, supplier, Set.of()));
        Dog dog2 = dogRepository.create(new Dog(null, "Bella", "Labrador", "K9-2", Gender.FEMALE, LocalDate.of(2021, 2, 2), LocalDate.of(2022, 2, 2), DogStatus.IN_SERVICE, null, null, supplier, Set.of()));
        Dog dog3 = dogRepository.create(new Dog(null, "Cooper", "Belgian Malinois", "K9-3", Gender.MALE, LocalDate.of(2022, 3, 3), LocalDate.of(2023, 3, 3), DogStatus.IN_SERVICE, null, null, supplier, Set.of()));

        DogPage page0 = dogRepository.findAll(new DogFilter(), new DogPageRequest(0, 2));
        assertNotNull(page0);
        assertEquals(0, page0.page());
        assertEquals(2, page0.size());
        assertTrue(page0.totalElements() >= 3);
        assertEquals(2, page0.content().size());

        DogPage page1 = dogRepository.findAll(new DogFilter(), new DogPageRequest(1, 2));
        assertNotNull(page1);
        assertEquals(1, page1.page());
        assertEquals(2, page1.size());
        assertTrue(page1.content().size() >= 1);
    }

    @Test
    void shouldExcludeSoftDeletedDogsFromSearch() {
        Supplier supplier = new Supplier(null, "Scottish Police Dog Training");
        Dog dog1 = dogRepository.create(new Dog(null, "UniqueActiveDog", "Bloodhound", "K9-901", Gender.MALE, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 1, 1), DogStatus.IN_SERVICE, null, null, supplier, Set.of()));
        Dog dog2 = dogRepository.create(new Dog(null, "UniqueDeletedDog", "Bloodhound", "K9-902", Gender.MALE, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 1, 1), DogStatus.IN_SERVICE, null, null, supplier, Set.of()));

        dogRepository.softDelete(dog2.id());

        DogPage result = dogRepository.findAll(new DogFilter(null, "Bloodhound", null), new DogPageRequest(0, 10));
        List<String> names = result.content().stream().map(Dog::name).toList();

        assertTrue(names.contains("UniqueActiveDog"));
        assertFalse(names.contains("UniqueDeletedDog"));
    }

    @Test
    void shouldFilterByNameCaseInsensitiveAndPartialMatch() {
        Supplier supplier = new Supplier(null, "Midlands Canine Training");
        dogRepository.create(new Dog(null, "Thorin Oakenshield", "Rottweiler", "K9-801", Gender.MALE, LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1), DogStatus.IN_SERVICE, null, null, supplier, Set.of()));

        DogPage result = dogRepository.findAll(new DogFilter("horin", null, null), new DogPageRequest(0, 10));
        assertTrue(result.content().stream().anyMatch(d -> "Thorin Oakenshield".equals(d.name())));

        DogPage upperResult = dogRepository.findAll(new DogFilter("THORIN", null, null), new DogPageRequest(0, 10));
        assertTrue(upperResult.content().stream().anyMatch(d -> "Thorin Oakenshield".equals(d.name())));
    }

    @Test
    void shouldFilterByBreedCaseInsensitiveAndPartialMatch() {
        Supplier supplier = new Supplier(null, "Welsh Working Dogs");
        dogRepository.create(new Dog(null, "Flash", "Dutch Shepherd", "K9-701", Gender.MALE, LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1), DogStatus.IN_SERVICE, null, null, supplier, Set.of()));

        DogPage result = dogRepository.findAll(new DogFilter(null, "dutch", null), new DogPageRequest(0, 10));
        assertTrue(result.content().stream().anyMatch(d -> "Flash".equals(d.name())));
    }

    @Test
    void shouldFilterBySupplierCaseInsensitiveAndPartialMatch() {
        Supplier supplier = new Supplier(null, "Highland Search & Rescue Kennels");
        dogRepository.create(new Dog(null, "Skye", "Border Collie", "K9-601", Gender.FEMALE, LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1), DogStatus.IN_SERVICE, null, null, supplier, Set.of()));

        DogPage result = dogRepository.findAll(new DogFilter(null, null, "highland"), new DogPageRequest(0, 10));
        assertTrue(result.content().stream().anyMatch(d -> "Skye".equals(d.name())));
    }

    @Test
    void shouldFilterByMultipleCriteriaWithAndSemantics() {
        Supplier supplier1 = new Supplier(null, "Alpha K9 Breeders");
        Supplier supplier2 = new Supplier(null, "Beta K9 Breeders");

        dogRepository.create(new Dog(null, "Diesel", "German Shepherd", "K9-501", Gender.MALE, LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1), DogStatus.IN_SERVICE, null, null, supplier1, Set.of()));
        dogRepository.create(new Dog(null, "Diesel", "Belgian Malinois", "K9-502", Gender.MALE, LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1), DogStatus.IN_SERVICE, null, null, supplier2, Set.of()));

        // Match Diesel + German Shepherd + Alpha -> only dog 1
        DogPage matchingResult = dogRepository.findAll(new DogFilter("Diesel", "German", "Alpha"), new DogPageRequest(0, 10));
        assertEquals(1, matchingResult.content().size());
        assertEquals("Diesel", matchingResult.content().get(0).name());
        assertEquals("German Shepherd", matchingResult.content().get(0).breed());
        assertEquals("Alpha K9 Breeders", matchingResult.content().get(0).supplier().name());

        // Match Diesel + German Shepherd + Beta -> 0 matches (AND semantics)
        DogPage nonMatchingResult = dogRepository.findAll(new DogFilter("Diesel", "German", "Beta"), new DogPageRequest(0, 10));
        assertEquals(0, nonMatchingResult.content().size());
        assertEquals(0, nonMatchingResult.totalElements());
    }

    @Test
    void shouldIgnoreBlankAndNullFilterCriteria() {
        Supplier supplier = new Supplier(null, "Gamma Kennels");
        dogRepository.create(new Dog(null, "Ghost", "Husky", "K9-401", Gender.MALE, LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1), DogStatus.IN_SERVICE, null, null, supplier, Set.of()));

        DogPage blankFilterResult = dogRepository.findAll(new DogFilter("   ", "", null), new DogPageRequest(0, 50));
        assertTrue(blankFilterResult.totalElements() > 0);
        assertTrue(blankFilterResult.content().stream().anyMatch(d -> "Ghost".equals(d.name())));
    }
}
