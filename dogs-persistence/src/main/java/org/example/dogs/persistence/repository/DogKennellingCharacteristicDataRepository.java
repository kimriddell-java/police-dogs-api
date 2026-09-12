package org.example.dogs.persistence.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;
import org.example.dogs.persistence.entity.DogKennellingCharacteristicEntity;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DogKennellingCharacteristicDataRepository extends GenericRepository<DogKennellingCharacteristicEntity, Long> {

    DogKennellingCharacteristicEntity save(DogKennellingCharacteristicEntity entity);

    List<DogKennellingCharacteristicEntity> findByDogId(Long dogId);

    @Query("DELETE FROM dog_kennelling_characteristic WHERE dog_id = :dogId")
    void deleteByDogId(Long dogId);
}
