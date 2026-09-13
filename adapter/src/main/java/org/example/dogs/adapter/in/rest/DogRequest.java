package org.example.dogs.adapter.in.rest;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotBlank;
import org.example.dogs.model.DogStatus;
import org.example.dogs.model.Gender;
import org.example.dogs.model.KennellingCharacteristic;
import org.example.dogs.model.LeavingReason;
import org.example.dogs.model.Supplier;

import java.time.LocalDate;
import java.util.Set;

@Introspected
public record DogRequest(
        @NotBlank
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