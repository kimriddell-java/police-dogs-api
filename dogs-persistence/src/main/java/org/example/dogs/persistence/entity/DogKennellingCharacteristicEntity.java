package org.example.dogs.persistence.entity;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;

@MappedEntity("dog_kennelling_characteristic")
public class DogKennellingCharacteristicEntity {

    @MappedProperty("dog_id")
    private Long dogId;

    @MappedProperty("characteristic_id")
    private Long characteristicId;

    public DogKennellingCharacteristicEntity() {
    }

    public DogKennellingCharacteristicEntity(Long dogId, Long characteristicId) {
        this.dogId = dogId;
        this.characteristicId = characteristicId;
    }

    public Long getDogId() {
        return dogId;
    }

    public void setDogId(Long dogId) {
        this.dogId = dogId;
    }

    public Long getCharacteristicId() {
        return characteristicId;
    }

    public void setCharacteristicId(Long characteristicId) {
        this.characteristicId = characteristicId;
    }
}
