package ru.yandex.practicum.filmorate.integration.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbTest {

    User user1;
    User user2;
    final UserDbStorage userStorage;

    @BeforeEach
    void beforeEach() {
        createEnvironment();
        userStorage.deleteAll();
    }

    @AfterEach
    void afterEach() {
        userStorage.deleteAll();
    }

    public void createEnvironment() {
        user1 = User.builder()
                .id(0)
                .email("dee.irk@gmail.com")
                .login("dee")
                .name("Дмитрий")
                .birthday(LocalDate.of(1982, 6, 6))
                .build();
        user2 = User.builder()
                .id(0)
                .email("volozh@yandex.ru")
                .login("volozh")
                .name("Аркадий")
                .birthday(LocalDate.of(1964, 2, 11))
                .build();
    }

    @Test
    void testSaveUser() {
        assertThat(user1.getId()).isZero();

        final User savedUser = userStorage.save(user1);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotZero();
    }

    @Test
    void testFindById() {
        User savedUser = userStorage.save(user1);

        final Optional<User> userOptional = userStorage.findById(savedUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", savedUser.getId()));
    }

    @Test
    void testGetAll() {
        userStorage.save(user1);
        userStorage.save(user2);

        final List<User> users = userStorage.getAll();

        assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .isEqualTo(List.of(user1, user2));
    }

    @Test
    void testGetAllById() {
        final long user1Id = userStorage.save(user1).getId();
        final long user2Id = userStorage.save(user2).getId();

        final List<User> users = userStorage.getAllById(List.of(user1Id, user2Id));

        assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .isEqualTo(List.of(user1, user2));
    }

    @Test
    void testDeleteById() {
        final long userId = userStorage.save(user1).getId();

        userStorage.deleteById(userId);
        Optional<User> optionalUser = userStorage.findById(userId);

        assertThat(optionalUser).isNotPresent();
    }

    @Test
    void testDeleteAllById() {
        final long userId = userStorage.save(user1).getId();
        final long friendId = userStorage.save(user2).getId();

        userStorage.deleteAllById(List.of(userId, friendId));
        final List<User> users = userStorage.getAll();

        assertThat(users)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void testExistsById() {
        assertFalse(userStorage.existsById(user1.getId()));

        final long userId = userStorage.save(user1).getId();

        assertTrue(userStorage.existsById(userId));
    }
}