package ru.job4j.cars.repository;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

public class HbmCarRepositoryTest {

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

    @AfterEach
    void tearDown() {
        if (sf != null) {
            sf.close();
        }
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Test
    public void whenSaveCarThenReturnCar() {
        TestDataFactory factory = new TestDataFactory(crudRepository);

        Engine engine = factory.createAndSaveEngine("V8");
        User user = factory.createAndSaveUser("user_" + UUID.randomUUID(), "securePassword");
        Owner owner = factory.createAndSaveOwner("John Doe", user);
        Car car = factory.createCar("BMW M3", engine, owner);
        HbmCarRepository repository = new HbmCarRepository(crudRepository);
        Car result = repository.saveCar(car);

        assertThat(result).isSameAs(car);
        assertThat(result.getName()).isEqualTo("BMW M3");
        assertThat(result.getEngine().getName()).isEqualTo("V8");
        assertThat(result.getOwner().getName()).isEqualTo("John Doe");
        assertThat(result.getOwner().getUser().getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    public void whenSaveCarsThenGetAllCars() {
        TestDataFactory factory = new TestDataFactory(crudRepository);

        Engine engine1 = factory.createAndSaveEngine("V8");
        Engine engine2 = factory.createAndSaveEngine("V6");

        User user1 = factory.createAndSaveUser("user_" + UUID.randomUUID(), "password1");
        User user2 = factory.createAndSaveUser("user_" + UUID.randomUUID(), "password2");

        Owner owner1 = factory.createAndSaveOwner("John Doe", user1);
        Owner owner2 = factory.createAndSaveOwner("Jane Smith", user2);

        Car car1 = factory.createCar("BMW M3", engine1, owner1);
        Car car2 = factory.createCar("Audi A4", engine2, owner2);

        HbmCarRepository repository = new HbmCarRepository(crudRepository);
        repository.saveCar(car1);
        repository.saveCar(car2);

        List<Car> cars = repository.getAll();
        assertThat(cars).isNotNull()
                .hasSizeGreaterThanOrEqualTo(2)
                .extracting(Car::getName)
                .contains("BMW M3", "Audi A4");
    }

    @Test
    public void whenFindCarByIdThenReturnCar() {
        TestDataFactory factory = new TestDataFactory(crudRepository);

        Engine engine = factory.createAndSaveEngine("V8");
        User user = factory.createAndSaveUser("user_" + UUID.randomUUID(), "securePassword");
        Owner owner = factory.createAndSaveOwner("John Doe", user);

        Car car = new Car();
        car.setName("BMW M3");
        car.setEngine(engine);
        car.setOwner(owner);
        crudRepository.run(session -> session.save(car));

        HistoryOwners historyOwner = new HistoryOwners();
        historyOwner.setOwner(owner);
        historyOwner.setCar(car);
        crudRepository.run(session -> session.save(historyOwner));

        AtomicReference<Car> managedCarRef = new AtomicReference<>();
        crudRepository.run(session -> {
            Car c = session.get(Car.class, car.getId());
            Hibernate.initialize(c.getOwner().getHistoryOwners());
            managedCarRef.set(c);
        });

        Car managedCar = managedCarRef.get();
        assertThat(managedCar).isNotNull();
        assertThat(managedCar.getOwner()).isNotNull();
        assertThat(managedCar.getOwner().getHistoryOwners()).isNotEmpty();
    }
}