package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class HbmEngineRepositoryTest {

    private CrudRepository crudRepository;

    private StandardServiceRegistry registry;

    private SessionFactory sf;

    @BeforeEach
    void setUp() {
        registry = new StandardServiceRegistryBuilder()
                .configure("hibernate-test.cfg.xml")
                .build();
        sf = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
        crudRepository = new CrudRepository(sf);
        cleanDatabase();
    }

    void cleanDatabase() {
        crudRepository.run(session -> {
            session.createQuery("DELETE FROM HistoryOwners").executeUpdate();
            session.createQuery("DELETE FROM Car").executeUpdate();
            session.createQuery("DELETE FROM Owner").executeUpdate();
            session.createQuery("DELETE FROM User").executeUpdate();
            session.createQuery("DELETE FROM Engine").executeUpdate();
        });
    }

    @Test
    public void whenSaveEnginesThenGetAllEngines() {
        TestDataFactory factory = new TestDataFactory(crudRepository);
        HbmEngineRepository engineRepository = new HbmEngineRepository(crudRepository);

        Engine engine1 = factory.createAndSaveEngine("V8");
        Engine engine2 = factory.createAndSaveEngine("V6");

        List<Engine> engines = engineRepository.getAll();

        assertThat(engines)
                .isNotNull()
                .isNotEmpty()
                .extracting(Engine::getName)
                .contains("V8", "V6");
    }

    @Test
    public void whenSaveEnginesThenFindEngineById() {
        TestDataFactory factory = new TestDataFactory(crudRepository);
        HbmEngineRepository engineRepository = new HbmEngineRepository(crudRepository);

        Engine engine1 = factory.createAndSaveEngine("V8");
        Engine engine2 = factory.createAndSaveEngine("V6");

        Optional<Engine> foundEngine = engineRepository.findEngineById(engine1.getId());

        assertThat(foundEngine)
                .isPresent()
                .get()
                .extracting(Engine::getName)
                .isEqualTo("V8");
    }
}
