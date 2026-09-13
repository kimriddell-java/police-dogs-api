package org.example.dogs.model;

import java.time.LocalDate;
import java.util.Set;

public record Dog(
        Long id,
        String name,
        String breed,
        String badgeId,
        Gender gender,
        LocalDate birthDate,
        LocalDate dateAcquired,
        DogStatus currentStatus,
        LocalDate leavingDate,
        LeavingReason leavingReason,
        Supplier supplier,
        Set<KennellingCharacteristic> kennellingCharacteristics
) {
    public Dog(Long id, String name, String breed) {
        this(id, name, breed, null, null, null, null, null, null, null, null, Set.of());
    }
}
