package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenreType;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDaoTest {

    final GenreStorage genreStorage;

    @Test
    void testFindById() {
        final int id = 1;

        final Optional<Genre> genreOptional = genreStorage.findById(id);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre)
                                .hasFieldOrPropertyWithValue("name", GenreType.values()[id - 1].getName())
                                .hasFieldOrPropertyWithValue("name", "Комедия"));
    }

    @Test
    void testFindAll() {
        final List<Genre> allGenresTypes = Arrays.stream(GenreType.values())
                .map(genreType -> new Genre(genreType.ordinal() + 1))
                .collect(Collectors.toList());

        final Collection<Genre> genres = genreStorage.getAll();

        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .hasSize(GenreType.values().length)
                .containsAll(allGenresTypes)
                .isEqualTo(allGenresTypes);
    }

    @Test
    void testExistsById() {
        final int id = new Random().nextInt(GenreType.values().length - 1) + 1;

        assertTrue(genreStorage.existsById(id));
    }

}