package org.example.dogs.persistence.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import org.example.dogs.persistence.entity.SupplierEntity;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SupplierDataRepository extends CrudRepository<SupplierEntity, Long> {

    Optional<SupplierEntity> findByName(String name);
}
