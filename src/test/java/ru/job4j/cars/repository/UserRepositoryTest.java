package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class UserRepositoryTest {
    private static SessionFactory sessionFactory;
    private UserRepository userRepository;

    @BeforeAll
    static void init() {
        sessionFactory = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(User.class)
                .buildSessionFactory();
    }

    @AfterAll
    static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository(sessionFactory);
        var session = sessionFactory.openSession();
        session.beginTransaction();
        session.createQuery("delete from Owner").executeUpdate();
        session.createQuery("delete from User").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Test
    void whenCreateUserThenUserIsSavedAndIdGenerated() {
        User user = new User();
        user.setLogin("userTest");
        user.setPassword("pass123");

        User savedUser = userRepository.create(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
        assertThat(savedUser.getLogin()).isEqualTo("userTest");
        assertThat(savedUser.getPassword()).isEqualTo("pass123");

        try (var session = sessionFactory.openSession()) {
            User userFromDb = session.get(User.class, savedUser.getId());
            assertThat(userFromDb).isNotNull();
            assertThat(userFromDb.getLogin()).isEqualTo("userTest");
            assertThat(userFromDb.getPassword()).isEqualTo("pass123");
        }
    }

    @Test
    void whenUpdateUserThenUserIsUpdated() {
        User user = new User();
        user.setLogin("userTest");
        user.setPassword("pass123");
        User savedUser = userRepository.create(user);
        savedUser.setLogin("updatedLogin");
        savedUser.setPassword("newPass456");

        userRepository.update(savedUser);

        try (var session = sessionFactory.openSession()) {
            User userFromDb = session.get(User.class, savedUser.getId());
            assertThat(userFromDb).isNotNull();
            assertThat(userFromDb.getLogin()).isEqualTo("updatedLogin");
            assertThat(userFromDb.getPassword()).isEqualTo("newPass456");
        }
    }

    @Test
    void whenDeleteUserThenUserIsRemoved() {
        User user = new User();
        user.setLogin("userToDelete");
        user.setPassword("pass123");
        User savedUser = userRepository.create(user);
        int userId = savedUser.getId();

        userRepository.delete(userId);

        try (var session = sessionFactory.openSession()) {
            User userFromDb = session.get(User.class, userId);
            assertThat(userFromDb).isNull();
        }
    }

    @Test
    void whenFindAllOrderByIdThenUsersAreReturnedInOrder() {
        User user1 = new User();
        user1.setLogin("user1");
        user1.setPassword("pass1");
        User savedUser1 = userRepository.create(user1);
        User user2 = new User();
        user2.setLogin("user2");
        user2.setPassword("pass2");
        User savedUser2 = userRepository.create(user2);
        User user3 = new User();
        user3.setLogin("user3");
        user3.setPassword("pass3");
        User savedUser3 = userRepository.create(user3);
        List<User> users = userRepository.findAllOrderById();

        assertThat(users).isNotEmpty();

        for (int i = 0; i < users.size() - 1; i++) {
            assertThat(users.get(i).getId()).isLessThan(users.get(i + 1).getId());
        }
    }

    @Test
    void whenFindByIdThenReturnUserOptional() {
        User user = new User();
        user.setLogin("userFindTest");
        user.setPassword("pass123");
        User savedUser = userRepository.create(user);
        int userId = savedUser.getId();

        Optional<User> found = userRepository.findById(userId);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(userId);
        assertThat(found.get().getLogin()).isEqualTo("userFindTest");
        assertThat(found.get().getPassword()).isEqualTo("pass123");

        try (var session = sessionFactory.openSession()) {
            User userFromDb = session.get(User.class, userId);
            assertThat(userFromDb).isNotNull();
            assertThat(userFromDb.getLogin()).isEqualTo("userFindTest");
        }
    }

    @Test
    void whenFindByLikeLoginThenReturnMatchingUsers() {
        User user1 = new User();
        user1.setLogin("Ivan Petrov");
        user1.setPassword("pass1");
        userRepository.create(user1);
        User user2 = new User();
        user2.setLogin("Maria Smirnova");
        user2.setPassword("pass2");
        userRepository.create(user2);
        User user3 = new User();
        user3.setLogin("Sergey Ivanov");
        user3.setPassword("pass3");
        userRepository.create(user3);

        String key = "Ivan";
        List<User> result = userRepository.findByLikeLogin(key);

        assertThat(result).hasSize(2);
        List<String> logins = result.stream().map(User::getLogin).collect(Collectors.toList());
        assertThat(logins).containsExactlyInAnyOrder("Ivan Petrov", "Sergey Ivanov");
    }

    @Test
    void whenFindByLoginThenReturnUser() {
        User user1 = new User();
        user1.setLogin("Ivan Petrov");
        user1.setPassword("pass1");
        userRepository.create(user1);
        User user2 = new User();
        user2.setLogin("Maria Smirnova");
        user2.setPassword("pass2");
        userRepository.create(user2);

        Optional<User> result = userRepository.findByLogin("Ivan Petrov");

        assertThat(result).isPresent();
        assertThat(result.get().getLogin()).isEqualTo("Ivan Petrov");
    }
}
