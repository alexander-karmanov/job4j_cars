package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmEngineRepository implements EngineRepository {

    private final CrudRepository crudRepository;

    @Override
    public List<Engine> getAll() {
        return crudRepository.query(
                "FROM Engine",
                Engine.class
        );
    }

    @Override
    public Optional<Engine> findEngineById(int id) {
        return crudRepository.optional(
                "FROM Engine WHERE id = :id",
                Engine.class,
                Map.of("id", id)
        );
    }
}
