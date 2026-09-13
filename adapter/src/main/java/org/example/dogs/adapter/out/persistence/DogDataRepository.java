package org.example.dogs.adapter.out.persistence;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DogDataRepository extends PageableRepository<DogEntity, Long> {

    Optional<DogEntity> findByIdAndDeletedFalse(Long id);

    @Query(
            value = "SELECT d.id, d.name, d.breed, d.badge_id, d.gender, d.birth_date, d.date_acquired, " +
                    "d.current_status, d.leaving_date, d.leaving_reason, d.supplier_id, " +
                    "d.deleted, d.deleted_at, d.created_at, d.updated_at " +
                    "FROM dog d LEFT JOIN supplier s ON d.supplier_id = s.id " +
                    "WHERE d.deleted = false " +
                    "AND (:name IS NULL OR d.name ILIKE CONCAT('%', :name, '%')) " +
                    "AND (:breed IS NULL OR d.breed ILIKE CONCAT('%', :breed, '%')) " +
                    "AND (:supplier IS NULL OR s.name ILIKE CONCAT('%', :supplier, '%')) " +
                    "ORDER BY d.id ASC",
            countQuery = "SELECT COUNT(d.id) FROM dog d LEFT JOIN supplier s ON d.supplier_id = s.id " +
                    "WHERE d.deleted = false " +
                    "AND (:name IS NULL OR d.name ILIKE CONCAT('%', :name, '%')) " +
                    "AND (:breed IS NULL OR d.breed ILIKE CONCAT('%', :breed, '%')) " +
                    "AND (:supplier IS NULL OR s.name ILIKE CONCAT('%', :supplier, '%'))"
    )
    Page<DogEntity> search(
            @Nullable String name,
            @Nullable String breed,
            @Nullable String supplier,
            Pageable pageable
    );
}
