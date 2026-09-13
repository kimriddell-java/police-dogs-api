package org.example.dogs.adapter.out.persistence;

import org.example.dogs.application.port.out.persistence.DogRepository;
import org.example.dogs.application.query.DogFilter;
import org.example.dogs.application.query.DogPage;
import org.example.dogs.application.query.DogPageRequest;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import org.example.dogs.model.Dog;
import org.example.dogs.model.KennellingCharacteristic;
import org.example.dogs.model.Supplier;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class JdbcDogRepository implements DogRepository {

    private final DogDataRepository dogDataRepository;
    private final SupplierDataRepository supplierDataRepository;
    private final KennellingCharacteristicDataRepository characteristicDataRepository;
    private final DogKennellingCharacteristicDataRepository dogCharacteristicDataRepository;
    private final DogPersistenceMapper dogPersistenceMapper;

    public JdbcDogRepository(
            DogDataRepository dogDataRepository,
            SupplierDataRepository supplierDataRepository,
            KennellingCharacteristicDataRepository characteristicDataRepository,
            DogKennellingCharacteristicDataRepository dogCharacteristicDataRepository,
            DogPersistenceMapper dogPersistenceMapper
    ) {
        this.dogDataRepository = dogDataRepository;
        this.supplierDataRepository = supplierDataRepository;
        this.characteristicDataRepository = characteristicDataRepository;
        this.dogCharacteristicDataRepository = dogCharacteristicDataRepository;
        this.dogPersistenceMapper = dogPersistenceMapper;
    }

    @Override
    public Optional<Dog> findById(Long id) {
        return dogDataRepository.findByIdAndDeletedFalse(id)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public Dog create(Dog dog) {
        Long supplierId = resolveSupplierId(dog.supplier());

        DogEntity entity = dogPersistenceMapper.toEntity(dog);
        entity.setSupplierId(supplierId);
        entity.setDeleted(false);

        DogEntity saved = dogDataRepository.save(entity);

        saveKennellingCharacteristics(saved.getId(), dog.kennellingCharacteristics());

        return findById(saved.getId()).orElseThrow();
    }

    @Override
    @Transactional
    public Dog update(Dog dog) {
        DogEntity entity = dogDataRepository.findByIdAndDeletedFalse(dog.id())
                .orElseThrow(() -> new IllegalArgumentException("Dog not found: " + dog.id()));

        Long supplierId = resolveSupplierId(dog.supplier());

        dogPersistenceMapper.updateEntityFromDomain(dog, entity);
        entity.setSupplierId(supplierId);

        dogDataRepository.update(entity);

        dogCharacteristicDataRepository.deleteByDogId(dog.id());
        saveKennellingCharacteristics(dog.id(), dog.kennellingCharacteristics());

        return findById(dog.id()).orElseThrow();
    }

    @Override
    public void softDelete(Long id) {
        DogEntity entity = dogDataRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found: " + id));
        entity.setDeleted(true);
        entity.setDeletedAt(Instant.now());
        dogDataRepository.update(entity);
    }

    @Override
    public DogPage findAll(DogFilter filter, DogPageRequest pageRequest) {
        if (pageRequest == null) {
            pageRequest = new DogPageRequest(0, 10);
        }

        String name = normaliseFilterValue(filter != null ? filter.name() : null);
        String breed = normaliseFilterValue(filter != null ? filter.breed() : null);
        String supplier = normaliseFilterValue(filter != null ? filter.supplier() : null);

        Pageable pageable = Pageable.from(pageRequest.page(), pageRequest.size());
        Page<DogEntity> entityPage = dogDataRepository.search(name, breed, supplier, pageable);

        List<Dog> dogs = entityPage.getContent().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());

        return DogPage.of(dogs, pageRequest, entityPage.getTotalSize());
    }

    private void saveKennellingCharacteristics(Long dogId, Set<KennellingCharacteristic> characteristics) {
        if (characteristics != null && !characteristics.isEmpty()) {
            for (KennellingCharacteristic characteristic : characteristics) {
                Long charId = resolveCharacteristicId(characteristic);
                dogCharacteristicDataRepository.save(new DogKennellingCharacteristicEntity(dogId, charId));
            }
        }
    }

    private String normaliseFilterValue(String value) {
        return (value != null && !value.isBlank()) ? value.trim() : null;
    }

    private Long resolveSupplierId(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        if (supplier.id() != null) {
            return supplier.id();
        }
        if (supplier.name() != null && !supplier.name().isBlank()) {
            return supplierDataRepository.findByName(supplier.name())
                    .map(SupplierEntity::getId)
                    .orElseGet(() -> supplierDataRepository.save(new SupplierEntity(null, supplier.name())).getId());
        }
        return null;
    }

    private Long resolveCharacteristicId(KennellingCharacteristic characteristic) {
        if (characteristic.id() != null) {
            return characteristic.id();
        }
        if (characteristic.name() != null && !characteristic.name().isBlank()) {
            return characteristicDataRepository.findByName(characteristic.name())
                    .map(KennellingCharacteristicEntity::getId)
                    .orElseGet(() -> characteristicDataRepository.save(new KennellingCharacteristicEntity(null, characteristic.name())).getId());
        }
        throw new IllegalArgumentException("Characteristic must have an id or name");
    }

    private Dog toDomain(DogEntity entity) {
        Supplier supplier = null;
        if (entity.getSupplierId() != null) {
            supplier = supplierDataRepository.findById(entity.getSupplierId())
                    .map(s -> new Supplier(s.getId(), s.getName()))
                    .orElse(null);
        }

        List<DogKennellingCharacteristicEntity> links = dogCharacteristicDataRepository.findByDogId(entity.getId());
        Set<KennellingCharacteristic> characteristics;
        if (links.isEmpty()) {
            characteristics = Collections.emptySet();
        } else {
            Set<Long> charIds = links.stream().map(DogKennellingCharacteristicEntity::getCharacteristicId).collect(Collectors.toSet());
            characteristics = characteristicDataRepository.findByIdIn(charIds).stream()
                    .map(c -> new KennellingCharacteristic(c.getId(), c.getName()))
                    .collect(Collectors.toSet());
        }

        return dogPersistenceMapper.toDomain(entity, supplier, characteristics);
    }
}
