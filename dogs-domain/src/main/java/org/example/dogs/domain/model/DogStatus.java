package org.example.dogs.domain.model;

public enum DogStatus {
    IN_TRAINING("In Training"),
    IN_SERVICE("In Service"),
    RETIRED("Retired"),
    LEFT("Left");

    private final String displayName;

    DogStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
