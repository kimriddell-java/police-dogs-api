package org.example.dogs.persistence.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import org.example.dogs.persistence.entity.KennellingCharacteristicEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface KennellingCharacteristicDataRepository extends CrudRepository<KennellingCharacteristicEntity, Long> {

    Optional<KennellingCharacteristicEntity> findByName(String name);

    List<KennellingCharacteristicEntity> findByIdIn(Set<Long> ids);
}
