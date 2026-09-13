package org.example.dogs.api.mapper;

import org.example.dogs.api.model.DogRequest;
import org.example.dogs.domain.model.Dog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.JAKARTA,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DogApiMapper {

    @Mapping(target = "id", ignore = true)
    Dog toDomain(DogRequest request);

    @Mapping(target = "id", source = "id")
    Dog toDomain(DogRequest request, Long id);
}