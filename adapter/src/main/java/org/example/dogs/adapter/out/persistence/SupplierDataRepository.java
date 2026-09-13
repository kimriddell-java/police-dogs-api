package org.example.dogs.adapter.out.persistence;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SupplierDataRepository extends CrudRepository<SupplierEntity, Long> {

    Optional<SupplierEntity> findByName(String name);
}
