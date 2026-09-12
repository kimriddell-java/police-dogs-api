package org.example.dogs.persistence.mapper;

import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.model.DogStatus;
import org.example.dogs.domain.model.Gender;
import org.example.dogs.domain.model.KennellingCharacteristic;
import org.example.dogs.domain.model.LeavingReason;
import org.example.dogs.domain.model.Supplier;
import org.example.dogs.persistence.entity.DogEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DogPersistenceMapperTest {

    private final DogPersistenceMapper mapper = new DogPersistenceMapperImpl();

    @Test
    void shouldMapDomainToEntity() {
        Dog dog = new Dog(
                1L,
                "Rex",
                "German Shepherd",
                "K9-01",
                Gender.MALE,
                LocalDate.of(2020, 1, 15),
                LocalDate.of(2021, 6, 1),
                DogStatus.IN_SERVICE,
                null,
                null,
                new Supplier(10L, "K9 Breeder"),
                Set.of(new KennellingCharacteristic(20L, "High energy"))
        );

        DogEntity entity = mapper.toEntity(dog);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("Rex", entity.getName());
        assertEquals("German Shepherd", entity.getBreed());
        assertEquals("K9-01", entity.getBadgeId());
        assertEquals(Gender.MALE, entity.getGender());
        assertEquals(LocalDate.of(2020, 1, 15), entity.getBirthDate());
        assertEquals(LocalDate.of(2021, 6, 1), entity.getDateAcquired());
        assertEquals(DogStatus.IN_SERVICE, entity.getCurrentStatus());
        assertNull(entity.getLeavingDate());
        assertNull(entity.getLeavingReason());
        assertNull(entity.getSupplierId());
    }

    @Test
    void shouldUpdateEntityFromDomain() {
        DogEntity entity = new DogEntity();
        entity.setId(1L);
        entity.setName("Rex");
        entity.setSupplierId(10L);

        Dog updated = new Dog(
                1L,
                "Rex II",
                "Belgian Malinois",
                "K9-02",
                Gender.FEMALE,
                LocalDate.of(2020, 3, 10),
                LocalDate.of(2021, 8, 1),
                DogStatus.RETIRED,
                LocalDate.of(2026, 1, 1),
                LeavingReason.RETIRED_REHOUSED,
                null,
                Set.of()
        );

        mapper.updateEntityFromDomain(updated, entity);

        assertEquals(1L, entity.getId()); // id should not be overwritten
        assertEquals("Rex II", entity.getName());
        assertEquals("Belgian Malinois", entity.getBreed());
        assertEquals("K9-02", entity.getBadgeId());
        assertEquals(Gender.FEMALE, entity.getGender());
        assertEquals(LocalDate.of(2020, 3, 10), entity.getBirthDate());
        assertEquals(LocalDate.of(2021, 8, 1), entity.getDateAcquired());
        assertEquals(DogStatus.RETIRED, entity.getCurrentStatus());
        assertEquals(LocalDate.of(2026, 1, 1), entity.getLeavingDate());
        assertEquals(LeavingReason.RETIRED_REHOUSED, entity.getLeavingReason());
        assertEquals(10L, entity.getSupplierId()); // preserved until explicitly updated
    }

    @Test
    void shouldMapEntityToDomain() {
        DogEntity entity = new DogEntity();
        entity.setId(5L);
        entity.setName("Buster");
        entity.setBreed("Labrador");
        entity.setBadgeId("K9-05");
        entity.setGender(Gender.MALE);
        entity.setBirthDate(LocalDate.of(2019, 5, 20));
        entity.setDateAcquired(LocalDate.of(2020, 10, 1));
        entity.setCurrentStatus(DogStatus.IN_SERVICE);

        Supplier supplier = new Supplier(100L, "Acme Kennels");
        Set<KennellingCharacteristic> characteristics = Set.of(
                new KennellingCharacteristic(200L, "Food guarder")
        );

        Dog domain = mapper.toDomain(entity, supplier, characteristics);

        assertNotNull(domain);
        assertEquals(5L, domain.id());
        assertEquals("Buster", domain.name());
        assertEquals("Labrador", domain.breed());
        assertEquals("K9-05", domain.badgeId());
        assertEquals(Gender.MALE, domain.gender());
        assertEquals(LocalDate.of(2019, 5, 20), domain.birthDate());
        assertEquals(LocalDate.of(2020, 10, 1), domain.dateAcquired());
        assertEquals(DogStatus.IN_SERVICE, domain.currentStatus());
        assertNull(domain.leavingDate());
        assertNull(domain.leavingReason());
        assertEquals(supplier, domain.supplier());
        assertEquals(characteristics, domain.kennellingCharacteristics());
    }
}
