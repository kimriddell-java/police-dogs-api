package org.example.dogs.api.model;

import org.example.dogs.domain.model.DogStatus;
import org.example.dogs.domain.model.Gender;
import org.example.dogs.domain.model.KennellingCharacteristic;
import org.example.dogs.domain.model.LeavingReason;
import org.example.dogs.domain.model.Supplier;

import java.time.LocalDate;
import java.util.Set;

public record DogRequest(
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
}