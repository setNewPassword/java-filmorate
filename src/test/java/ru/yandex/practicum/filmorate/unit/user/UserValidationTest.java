package ru.yandex.practicum.filmorate.unit.user;

import ru.yandex.practicum.filmorate.model.User;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {
    static Validator validator;
    User user;

    @BeforeAll
    public static void getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void getValidUser() {
        user = User.builder()
                .email("dee.irk@gmail.com")
                .login("dee")
                .name("Дмитрий")
                .birthday(LocalDate.of(1982, 6, 6))
                .build();
    }

    @Test
    @DisplayName("Email without @")
    public void shouldConstraintViolationWhenEmailWithoutAt() {
        user.setEmail(user.getEmail().replace("@", ""));
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Электронная почта не содержит «@».");
    }

    @Test
    @DisplayName("Email is empty")
    public void shouldConstraintViolationWhenEmailIsEmpty() {
        user.setEmail("");
        var violation = validator.validate(user);

        assertFalse(violation.isEmpty(), "Пустое значение поля «email».");
    }

    @Test
    @DisplayName("Login is empty")
    public void shouldConstraintViolationWhenLoginIsEmpty() {
        user.setLogin("");
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Пустое значение поля «login».");
    }

    @Test
    @DisplayName("Login is null")
    public void shouldConstraintViolationWhenLoginIsNull() {
        user.setLogin(null);
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Значение поля «login» — null.");
    }

    @Test
    @DisplayName("Login contains space")
    public void shouldConstraintViolationWhenLoginContainsSpace() {
        user.setLogin("Дмитрий Евтюхин");
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Логин содержит пробел.");
    }

    @Test
    @DisplayName("User's birthday in the future")
    public void shouldConstraintViolationWhenBirthdayInTheFuture() {
        user.setBirthday(LocalDate.now().plusYears(1000));
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Добро пожаловать на вечеринку путешественников во времени.");
    }

    @Test
    void shouldAddUser2IdInList() {
        final long user2Id = user.getId() + 1;
        user.addFriendId(user2Id);
        final List<Long> friends = user.getFriends();

        assertFalse(friends.isEmpty());
        assertEquals(1, friends.size());
        assertEquals(List.of(user2Id), friends);
        assertTrue(friends.contains(user2Id));
    }

    @Test
    void shouldDeleteUser2IdFromList() {
        final long user2Id = user.getId() + 1;
        user.addFriendId(user2Id);
        user.deleteFriendId(user2Id);
        final List<Long> friends = user.getFriends();

        assertTrue(friends.isEmpty());
        assertEquals(Collections.emptyList(), friends);
        assertFalse(friends.contains(user2Id));
    }

    @Test
    void shouldReturnFriendsList() {
        final List<Long> expectedList = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            user.addFriendId(i);
            expectedList.add(i);
        }
        final List<Long> returnedList = user.getFriends();

        assertFalse(returnedList.isEmpty());
        assertEquals(expectedList.size(), returnedList.size());
        assertEquals(expectedList, returnedList);
        assertTrue(returnedList.containsAll(expectedList));
    }

    @Test
    void shouldClearFriendsList() {
        final long user2Id = user.getId() + 1;
        user.addFriendId(user2Id);
        user.clearFriendList();
        final List<Long> clearList = user.getFriends();

        assertTrue(clearList.isEmpty());
    }
}