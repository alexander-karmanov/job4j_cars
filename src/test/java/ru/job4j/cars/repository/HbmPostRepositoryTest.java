package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class HbmPostRepositoryTest {

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
            session.createQuery("DELETE FROM Post").executeUpdate();
        });
    }

    @Test
    public void whenFindPostLastDayThenReturnPostsList() {
        TestDataFactory factory = new TestDataFactory(crudRepository);
        HbmPostRepository postRepository = new HbmPostRepository(crudRepository);

        LocalDateTime yesterdayAt10am = LocalDate.now().minusDays(1).atTime(10, 0);
        factory.createAndSavePost("Post from yesterday", yesterdayAt10am);

        LocalDateTime twoDaysAgo = LocalDate.now().minusDays(2).atTime(10, 0);
        factory.createAndSavePost("Post from two days ago", twoDaysAgo);

        List<Post> posts = postRepository.findPostLastDay();

        assertThat(posts)
                .isNotNull()
                .isNotEmpty()
                .extracting(Post::getDescription)
                .containsExactly("Post from yesterday");
    }
}
