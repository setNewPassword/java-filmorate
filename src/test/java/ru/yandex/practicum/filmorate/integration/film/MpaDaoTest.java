package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.MpaType;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDaoTest {

    final MpaStorage mpaStorage;

    @Test
    void testFindById() {
        final int id = 5;
        final Optional<Mpa> mpaOptional = mpaStorage.findById(id);

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa)
                                .hasFieldOrPropertyWithValue("name", MpaType.values()[id - 1].getName())
                                .hasFieldOrPropertyWithValue("name", "NC-17"));
    }

    @Test
    void testFindAll() {
        List<Mpa> allMpaRatings = Arrays.stream(MpaType.values())
                .map(mpaRating -> new Mpa(mpaRating.ordinal() + 1))
                .collect(Collectors.toList());

        final Collection<Mpa> ratings = mpaStorage.getAll();

        assertThat(ratings)
                .isNotNull()
                .isNotEmpty()
                .hasSize(MpaType.values().length)
                .containsAll(allMpaRatings)
                .isEqualTo(allMpaRatings);
    }

    @Test
    void testExistsById() {
        final int id = new Random().nextInt(MpaType.values().length - 1) + 1;

        assertTrue(mpaStorage.existsById(id));
    }

}