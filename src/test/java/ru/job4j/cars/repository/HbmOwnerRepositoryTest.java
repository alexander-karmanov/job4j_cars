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

public class HbmOwnerRepositoryTest {

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
    public void whenSaveOwnersThenGetAllOwners() {
        TestDataFactory factory = new TestDataFactory(crudRepository);
        HbmOwnerRepository ownerRepository = new HbmOwnerRepository(crudRepository);

        User user1 = factory.createAndSaveUser("User One", "user1@example.com");
        User user2 = factory.createAndSaveUser("User Two", "user2@example.com");

        Owner owner1 = factory.createAndSaveOwner("John Doe", user1);
        Owner owner2 = factory.createAndSaveOwner("Jane Smith", user2);

        List<Owner> owners = ownerRepository.getAll();

        assertThat(owners)
                .isNotNull()
                .isNotEmpty()
                .extracting(Owner::getName)
                .contains("John Doe", "Jane Smith");
    }

    @Test
    public void whenFindOwnerByIdThenReturnOwnerOptional() {
        TestDataFactory factory = new TestDataFactory(crudRepository);
        HbmOwnerRepository ownerRepository = new HbmOwnerRepository(crudRepository);

        User user = factory.createAndSaveUser("User Test", "user@example.ru");
        Owner savedOwner = factory.createAndSaveOwner("Ivan Semenov", user);

        Optional<Owner> foundOwnerOpt = ownerRepository.finaOwnerById(savedOwner.getId());

        assertThat(foundOwnerOpt)
                .isPresent()
                .get()
                .extracting(Owner::getName)
                .isEqualTo("Ivan Semenov");
    }
}
