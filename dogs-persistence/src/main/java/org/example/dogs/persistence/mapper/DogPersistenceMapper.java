package org.example.dogs.persistence.mapper;

import org.example.dogs.domain.model.Dog;
import org.example.dogs.domain.model.KennellingCharacteristic;
import org.example.dogs.domain.model.Supplier;
import org.example.dogs.persistence.entity.DogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DogPersistenceMapper {

    @Mapping(target = "id", source = "dog.id")
    @Mapping(target = "name", source = "dog.name")
    @Mapping(target = "breed", source = "dog.breed")
    @Mapping(target = "badgeId", source = "dog.badgeId")
    @Mapping(target = "gender", source = "dog.gender")
    @Mapping(target = "birthDate", source = "dog.birthDate")
    @Mapping(target = "dateAcquired", source = "dog.dateAcquired")
    @Mapping(target = "currentStatus", source = "dog.currentStatus")
    @Mapping(target = "leavingDate", source = "dog.leavingDate")
    @Mapping(target = "leavingReason", source = "dog.leavingReason")
    @Mapping(target = "supplierId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DogEntity toEntity(Dog dog);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "dog.name")
    @Mapping(target = "breed", source = "dog.breed")
    @Mapping(target = "badgeId", source = "dog.badgeId")
    @Mapping(target = "gender", source = "dog.gender")
    @Mapping(target = "birthDate", source = "dog.birthDate")
    @Mapping(target = "dateAcquired", source = "dog.dateAcquired")
    @Mapping(target = "currentStatus", source = "dog.currentStatus")
    @Mapping(target = "leavingDate", source = "dog.leavingDate")
    @Mapping(target = "leavingReason", source = "dog.leavingReason")
    @Mapping(target = "supplierId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDomain(Dog dog, @MappingTarget DogEntity entity);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "breed", source = "entity.breed")
    @Mapping(target = "badgeId", source = "entity.badgeId")
    @Mapping(target = "gender", source = "entity.gender")
    @Mapping(target = "birthDate", source = "entity.birthDate")
    @Mapping(target = "dateAcquired", source = "entity.dateAcquired")
    @Mapping(target = "currentStatus", source = "entity.currentStatus")
    @Mapping(target = "leavingDate", source = "entity.leavingDate")
    @Mapping(target = "leavingReason", source = "entity.leavingReason")
    @Mapping(target = "supplier", source = "supplier")
    @Mapping(target = "kennellingCharacteristics", source = "kennellingCharacteristics")
    Dog toDomain(DogEntity entity, Supplier supplier, Set<KennellingCharacteristic> kennellingCharacteristics);
}
