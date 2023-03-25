package ru.yandex.practicum.filmorate.model.film;

import ru.yandex.practicum.filmorate.model.Film;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.util.Arrays;

public class FilmValidationTest {
    static Validator validator;
    Film film;

    @BeforeAll
    public static void getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void getValidFilm() {
        film = Film.builder()
                .name("Titanic")
                .description("American epic romance and disaster film directed by James Cameron.")
                .releaseDate(LocalDate.of(1997, 12, 19))
                .duration(195)
                .build();
    }

    @Test
    @DisplayName("Film name is empty")
    public void shouldConstraintViolationWhenFilmNameIsEmpty() {
        film.setName("");
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Пустое значение поля «name».");
    }

    @Test
    @DisplayName("Film name is null")
    public void shouldConstraintViolationWhenFilmNameIsNull() {
        film.setName(null);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Значение поля «name» — null.");
    }

    @Test
    @DisplayName("Film description is null")
    public void shouldConstraintViolationWhenFilmDescriptionIsNull() {
        film.setDescription(null);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Значение поля «description» — null.");
    }

    @Test
    @DisplayName("Film description is longer than 200 characters")
    public void shouldConstraintViolationWhenFilmDescriptionIsLongerThan200Characters() {
        final char[] characterArray = new char[300];
        Arrays.fill(characterArray, 'J');
        final String longDescription = String.valueOf(characterArray);
        film.setDescription(longDescription);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Описание фильма длиннее 200 символов.");
    }

    @Test
    @DisplayName("The film's release date is before the first film's release date")
    public void shouldConstraintViolationWhenWrongFilmReleaseDate() {
        film.setReleaseDate(LocalDate.of(1234, 5, 7));
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Дата выпуска фильма раньше выпуска первого фильма в истории человечества.");
    }

    @Test
    @DisplayName("Film duration is not positive")
    public void shouldConstraintViolationWhenFilmDurationIsNotPositive() {
        film.setDuration(0);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Продолжительность фильма не положительная.");
    }
}
