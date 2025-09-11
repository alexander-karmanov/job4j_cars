package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Owner;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmOwnerRepository implements OwnerRepository {
    private final CrudRepository crudRepository;

    @Override
    public List<Owner> getAll() {
        return crudRepository.query(
                "FROM Owner",
                Owner.class
        );
    }

    @Override
    public Optional<Owner> finaOwnerById(int id) {
        return crudRepository.optional(
                "FROM Owner WHERE id = :id",
                Owner.class,
                Map.of("id", id)
        );
    }
}
