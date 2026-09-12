package org.example.dogs.persistence.entity;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity("kennelling_characteristic")
public class KennellingCharacteristicEntity {

    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private Long id;

    private String name;

    public KennellingCharacteristicEntity() {
    }

    public KennellingCharacteristicEntity(Long id, String name) {
        this.id = id;
        this.name = name;
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
}
