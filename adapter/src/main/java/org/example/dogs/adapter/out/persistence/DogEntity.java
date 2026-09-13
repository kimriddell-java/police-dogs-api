package org.example.dogs.adapter.out.persistence;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import org.example.dogs.model.DogStatus;
import org.example.dogs.model.Gender;
import org.example.dogs.model.LeavingReason;

import java.time.Instant;
import java.time.LocalDate;

@MappedEntity("dog")
public class DogEntity {

    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private Long id;

    private String name;
    private String breed;

    @MappedProperty("badge_id")
    private String badgeId;

    private Gender gender;

    @MappedProperty("birth_date")
    private LocalDate birthDate;

    @MappedProperty("date_acquired")
    private LocalDate dateAcquired;

    @MappedProperty("current_status")
    private DogStatus currentStatus;

    @MappedProperty("leaving_date")
    private LocalDate leavingDate;

    @MappedProperty("leaving_reason")
    private LeavingReason leavingReason;

    @MappedProperty("supplier_id")
    private Long supplierId;

    private boolean deleted;

    @MappedProperty("deleted_at")
    private Instant deletedAt;

    @DateCreated
    @MappedProperty("created_at")
    private Instant createdAt;

    @DateUpdated
    @MappedProperty("updated_at")
    private Instant updatedAt;

    public DogEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getDateAcquired() {
        return dateAcquired;
    }

    public void setDateAcquired(LocalDate dateAcquired) {
        this.dateAcquired = dateAcquired;
    }

    public DogStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(DogStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public LocalDate getLeavingDate() {
        return leavingDate;
    }

    public void setLeavingDate(LocalDate leavingDate) {
        this.leavingDate = leavingDate;
    }

    public LeavingReason getLeavingReason() {
        return leavingReason;
    }

    public void setLeavingReason(LeavingReason leavingReason) {
        this.leavingReason = leavingReason;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
